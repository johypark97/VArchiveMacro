package com.github.johypark97.varchivemacro.macro.ocr;

import java.awt.Rectangle;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.SizeTPointer;
import org.bytedeco.leptonica.BOX;
import org.bytedeco.leptonica.NUMA;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.leptonica.SEL;
import org.bytedeco.leptonica.global.leptonica;

public class PixWrapper implements AutoCloseable {
    public final PIX pixInstance;

    protected PixWrapper(PIX pixInstance) {
        this.pixInstance = pixInstance;
    }

    public PixWrapper(Path path) throws IOException {
        if (!Files.exists(path)) {
            throw new IOException(path + " (File not found)");
        }

        pixInstance = leptonica.pixRead(path.toString());
        if (pixInstance == null) {
            throw new IOException(path + " (Failed to read file)");
        }
    }

    public PixWrapper(byte[] pngBytes) throws PixError {
        pixInstance = leptonica.pixReadMemPng(pngBytes, pngBytes.length);
        if (pixInstance == null) {
            throw new PixError("Error: pixReadMemPng()");
        }
    }

    // -------------------------
    // -------- Getters --------
    // -------------------------

    public byte[] getPngBytes() throws PixError {
        try (BytePointer ptr_buffer = new BytePointer();
                SizeTPointer ptr_size_t = new SizeTPointer(1)) {
            if (leptonica.pixWriteMemPng(ptr_buffer, ptr_size_t, pixInstance, 0) != 0) {
                throw new PixError("Error: pixWriteMemPng()");
            }

            long size = ptr_size_t.get();
            byte[] bytes = new byte[(int) size];
            ptr_buffer.get(bytes);

            return bytes;
        }
    }

    public int getWidth() {
        return pixInstance.w();
    }

    public int getHeight() {
        return pixInstance.h();
    }

    // -------------------------------------
    // -------- Create new instance --------
    // -------------------------------------

    public PixWrapper copy() throws PixError {
        PIX pix = leptonica.pixCopy(null, pixInstance);
        if (pix == null) {
            throw new PixError("Error: pixCopy()");
        }

        return new PixWrapper(pix);
    }

    public PixWrapper crop(Rectangle rect) throws PixError {
        try (BOX box = leptonica.boxCreate(rect.x, rect.y, rect.width, rect.height)) {
            if (box == null) {
                throw new PixError("Error: boxCreate()");
            }

            PIX pix = leptonica.pixClipRectangle(pixInstance, box, (BOX) null);
            leptonica.boxDestroy(box);

            if (pix == null) {
                throw new PixError("Error: pixClipRectangle()");
            }

            return new PixWrapper(pix);
        }
    }

    // --------------------------------------------
    // -------- Image processing - general --------
    // --------------------------------------------

    public void contrastTRC(float factor) {
        // Return: pixd always
        leptonica.pixContrastTRC(pixInstance, pixInstance, factor);
    }

    public void convertRGBToLuminance() throws PixError {
        try (PIX p = leptonica.pixConvertRGBToLuminance(pixInstance)) {
            if (p == null) {
                throw new PixError("Error: pixConvertRGBToLuminance()");
            }

            PixError e = copyToPixInstance(p);
            leptonica.pixDestroy(p);

            if (e != null) {
                throw e;
            }
        }
    }

    public void gammaTRC(float gamma, int minval, int maxval) {
        // Return: pixd always
        leptonica.pixGammaTRC(pixInstance, pixInstance, gamma, minval, maxval);
    }

    public float[] getGrayHistogram(int factor) throws PixError {
        try (NUMA numa = leptonica.pixGetGrayHistogram(pixInstance, factor)) {
            if (numa == null) {
                throw new PixError("Error: pixGetGrayHistogram()");
            }

            int size = leptonica.numaGetCount(numa);
            if (size == 0) {
                leptonica.numaDestroy(numa);
                throw new PixError("Error: numaGetCount()");
            }

            float[] data;
            try (FloatPointer p = new FloatPointer(size)) {
                for (int i = 0; i < size; ++i) {
                    if (leptonica.numaGetFValue(numa, i, p.getPointer(i)) != 0) {
                        leptonica.numaDestroy(numa);
                        throw new PixError("Error: numaGetCount()");
                    }
                }

                data = new float[size];
                p.get(data);
            }

            leptonica.numaDestroy(numa);
            return data;
        }
    }

    public void invert() throws PixError {
        if (leptonica.pixInvert(pixInstance, pixInstance) == null) {
            throw new PixError("Error: pixInvert()");
        }
    }

    public void scaleGeneral(float scalex, float scaley, float sharpfract, int sharpwidth)
            throws PixError {
        try (PIX p = leptonica.pixScaleGeneral(pixInstance, scalex, scaley, sharpfract,
                sharpwidth)) {
            if (p == null) {
                throw new PixError("Error: pixScale()");
            }

            PixError e = copyToPixInstance(p);
            leptonica.pixDestroy(p);

            if (e != null) {
                throw e;
            }
        }
    }

    public boolean thresholdPixelSum(int thresh) throws PixError {
        try (IntPointer ptr_isAbove = new IntPointer(1)) {
            if (leptonica.pixThresholdPixelSum(pixInstance, thresh, ptr_isAbove, null) != 0) {
                throw new PixError("Error: pixThresholdPixelSum()");
            }

            return ptr_isAbove.get() == 1;
        }
    }

    public void thresholdToBinary(int thresh) throws PixError {
        try (PIX p = leptonica.pixThresholdToBinary(pixInstance, thresh)) {
            if (p == null) {
                throw new PixError("Error: pixThresholdToBinary()");
            }

            PixError e = copyToPixInstance(p);
            leptonica.pixDestroy(p);

            if (e != null) {
                throw e;
            }
        }
    }

    // -----------------------------------------------------
    // -------- Image processing - erode and dilate --------
    // -----------------------------------------------------

    public void erode(int originx, int originy, List<List<Integer>> kernel) throws PixError {
        try (SEL sel = createSel(kernel)) {
            sel.cx(originx);
            sel.cy(originy);

            // Return: pixd
            leptonica.pixErode(pixInstance, pixInstance, sel);

            leptonica.selDestroy(sel);
        }
    }

    public void dilate(int originx, int originy, List<List<Integer>> kernel) throws PixError {
        try (SEL sel = createSel(kernel)) {
            sel.cx(originx);
            sel.cy(originy);

            // Return: pixd
            leptonica.pixDilate(pixInstance, pixInstance, sel);

            leptonica.selDestroy(sel);
        }
    }

    protected SEL createSel(List<List<Integer>> kernel) throws PixError {
        int height = kernel.size();
        int width = kernel.stream().map(List::size).min(Integer::compareTo).orElse(0);

        SEL sel = leptonica.selCreate(height, width, (String) null);
        if (sel == null) {
            throw new PixError("Error: selCreate()");
        }

        for (int row = 0; row < height; ++row) {
            for (int column = 0; column < width; ++column) {
                int value = kernel.get(row).get(column);
                if (leptonica.selSetElement(sel, row, column, value) != 0) {
                    leptonica.selDestroy(sel);
                    sel.close();

                    throw new PixError("Error: selSetElement()");
                }
            }
        }

        return sel;
    }

    // ----------------------------------------------
    // -------- Image processing - protected --------
    // ----------------------------------------------

    protected PixError copyToPixInstance(PIX pix) {
        return (leptonica.pixCopy(pixInstance, pix) == null)
                ? new PixError("Error: pixCopy()")
                : null;
    }

    // ---------------------------
    // -------- Utilities --------
    // ---------------------------

    public float getGrayRatio(int factor, int threshold) throws PixError {
        float black;
        float sum = 0;
        float[] histogram = getGrayHistogram(factor);

        int length = histogram.length;
        int limit = Math.min(length, threshold);
        for (int i = 0; i < limit; ++i) {
            sum += histogram[i];
        }
        black = sum;
        for (int i = limit; i < length; ++i) {
            sum += histogram[i];
        }

        return black / sum;
    }

    // ----------------------------
    // -------- Implements --------
    // ----------------------------

    @Override
    public void close() {
        leptonica.pixDestroy(pixInstance);
        pixInstance.close();
    }
}
