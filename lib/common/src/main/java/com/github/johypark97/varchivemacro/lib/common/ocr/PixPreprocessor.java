package com.github.johypark97.varchivemacro.lib.common.ocr;

public class PixPreprocessor {
    public static void preprocessTitle(PixWrapper pix) throws PixError {
        pix.convertRGBToLuminance();
        pix.gammaTRC(1, 128, 255);
        pix.invert();
    }

    public static void preprocessCell(PixWrapper pix) throws PixError {
        pix.convertRGBToLuminance();
        pix.gammaTRC(0.2f, 0, 255);
        pix.invert();
    }
}
