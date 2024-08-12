package com.github.johypark97.varchivemacro.lib.scanner.ocr;

import java.nio.file.Path;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.tesseract.TessBaseAPI;

public class DefaultOcrWrapper implements OcrWrapper {
    private final TessBaseAPI tessBaseAPI;

    protected DefaultOcrWrapper(TessBaseAPI tessBaseAPI) {
        this.tessBaseAPI = tessBaseAPI;
    }

    public static DefaultOcrWrapper load(Path dataDir, String language)
            throws OcrInitializationError {
        TessBaseAPI tessBaseAPI = new TessBaseAPI();

        if (tessBaseAPI.Init(dataDir.toString(), language) != 0) {
            tessBaseAPI.End();
            throw new OcrInitializationError("OCR initialization failed.");
        }

        return new DefaultOcrWrapper(tessBaseAPI);
    }

    @Override
    public void setWhitelist(String whitelist) {
        tessBaseAPI.SetVariable("tessedit_char_whitelist", whitelist);
    }

    @Override
    public String run(PIX pix) {
        synchronized (this) {
            tessBaseAPI.SetImage(pix);

            try (BytePointer outText = tessBaseAPI.GetUTF8Text()) {
                return outText.getString();
            }
        }
    }

    @Override
    public void close() {
        tessBaseAPI.End();
    }
}
