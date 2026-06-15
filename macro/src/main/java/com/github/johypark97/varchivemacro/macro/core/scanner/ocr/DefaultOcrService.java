package com.github.johypark97.varchivemacro.macro.core.scanner.ocr;

import com.github.johypark97.varchivemacro.macro.core.scanner.piximage.model.PixImage;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.tesseract.TessBaseAPI;

public class DefaultOcrService implements OcrService {
    private final TessBaseAPI tessBaseAPI;

    public DefaultOcrService(String directory, String language) throws OcrInitializationError {
        tessBaseAPI = new TessBaseAPI();

        if (tessBaseAPI.Init(directory, language) != 0) {
            throw new OcrInitializationError("OCR initialization failed.");
        }
    }

    @Override
    public synchronized String run(PixImage pixImage) {
        tessBaseAPI.SetImage(pixImage.pixInstance);

        try (BytePointer outText = tessBaseAPI.GetUTF8Text()) {
            return outText.getString();
        }
    }

    @Override
    public void setWhitelist(String whitelist) {
        tessBaseAPI.SetVariable("tessedit_char_whitelist", whitelist);
    }

    @Override
    public void close() {
        tessBaseAPI.End();
    }
}
