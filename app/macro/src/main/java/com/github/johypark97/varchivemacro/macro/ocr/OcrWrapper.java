package com.github.johypark97.varchivemacro.macro.ocr;

import java.nio.file.Path;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.tesseract.TessBaseAPI;

public class OcrWrapper implements AutoCloseable {
    private static final Path DATAPATH = Path.of(System.getProperty("user.dir"), "data/tessdata");
    private static final String LANGUAGE = "eng";

    private final TessBaseAPI tessBaseAPI;

    public OcrWrapper() throws OcrInitializationError {
        tessBaseAPI = new TessBaseAPI();

        if (tessBaseAPI.Init(DATAPATH.toString(), LANGUAGE) != 0) {
            throw new OcrInitializationError("OCR initialization failed.");
        }
    }

    public void setWhitelist(String whitelist) {
        tessBaseAPI.SetVariable("tessedit_char_whitelist", whitelist);
    }

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
