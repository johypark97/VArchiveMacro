package com.github.johypark97.varchivemacro.macro.ocr;

import java.nio.file.Path;
import java.util.Map;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.tesseract.TessBaseAPI;

public class OcrWrapper implements AutoCloseable {
    private static final Path DATAPATH = Path.of(System.getProperty("user.dir"), "data/tessdata");
    private static final String LANGUAGE = "eng";

    private boolean isClosed;
    private final TessBaseAPI tessBaseAPI;

    public OcrWrapper() throws OcrInitializationError {
        tessBaseAPI = new TessBaseAPI();

        if (tessBaseAPI.Init(DATAPATH.toString(), LANGUAGE) != 0) {
            throw new OcrInitializationError("OCR initialization failed.");
        }
    }

    public synchronized void run(PIX pix, Map<?, OcrData> map) {
        if (isClosed) {
            return;
        }

        tessBaseAPI.SetImage(pix);
        map.values().forEach((x) -> {
            tessBaseAPI.SetRectangle(x.x, x.y, x.width, x.height);
            try (BytePointer outText = tessBaseAPI.GetUTF8Text()) {
                x.string = outText.getString();
            }
        });
    }

    @Override
    public synchronized void close() {
        tessBaseAPI.End();
        isClosed = true;
    }
}
