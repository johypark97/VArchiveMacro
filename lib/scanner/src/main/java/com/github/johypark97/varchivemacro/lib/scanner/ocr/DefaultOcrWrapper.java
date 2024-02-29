package com.github.johypark97.varchivemacro.lib.scanner.ocr;

import java.nio.file.Path;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.tesseract.TessBaseAPI;

public class DefaultOcrWrapper implements OcrWrapper {
    private final TessBaseAPI tessBaseAPI;

    public DefaultOcrWrapper(Path dataDir, String language) throws OcrInitializationError {
        tessBaseAPI = new TessBaseAPI();

        if (tessBaseAPI.Init(dataDir.toString(), language) != 0) {
            throw new OcrInitializationError("OCR initialization failed.");
        }
    }

    @Override
    public void setWhitelist(String whitelist) {
        tessBaseAPI.SetVariable("tessedit_char_whitelist", whitelist);
    }

    @Override
    public synchronized String run(PIX pix) {
        tessBaseAPI.SetImage(pix);
        try (BytePointer outText = tessBaseAPI.GetUTF8Text()) {
            return outText.getString();
        }
    }

    @Override
    public void close() {
        tessBaseAPI.End();
    }
}
