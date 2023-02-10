package com.github.johypark97.varchivemacro.macro.util;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.SizeTPointer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.leptonica.global.leptonica;

public class PixUtil {
    public static PIX pngbytes2pix(byte[] bytes) {
        return leptonica.pixReadMemPng(bytes, bytes.length);
    }

    public static byte[] pix2pngbytes(PIX pix) {
        try (BytePointer ptr_buffer = new BytePointer();
                SizeTPointer ptr_size_t = new SizeTPointer(1)) {
            if (leptonica.pixWriteMemPng(ptr_buffer, ptr_size_t, pix, 0) != 0) {
                return new byte[0];
            }

            byte[] ret = new byte[(int) ptr_size_t.get()];
            ptr_buffer.get(ret);
            return ret;
        }
    }

    public static PIX binarize(PIX pix, int thresh) {
        try (PIX p = leptonica.pixConvertTo8(pix, 0)) {
            return !p.isNull() ? leptonica.pixThresholdToBinary(p, thresh) : null;
        }
    }
}
