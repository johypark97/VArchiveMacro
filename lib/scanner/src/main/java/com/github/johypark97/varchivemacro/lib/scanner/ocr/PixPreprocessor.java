package com.github.johypark97.varchivemacro.lib.scanner.ocr;

import java.util.List;

public class PixPreprocessor {
    private static final List<List<Integer>> KERNEL33 =
            List.of(List.of(1, 1, 1), List.of(1, 1, 1), List.of(1, 1, 1));

    public static void thresholdWhite(PixWrapper pix) throws PixError {
        pix.convertRGBToLuminance();
        pix.thresholdToBinary(255);
    }

    public static void preprocessTitle(PixWrapper pix) throws PixError {
        preprocessTitle(pix, 1, 1, 1);
    }

    public static void preprocessTitle(PixWrapper pix, int dilation, float sx, float sy)
            throws PixError {
        thresholdWhite(pix);
        pix.invert();

        for (int i = 0; i < dilation; ++i) {
            pix.dilate(1, 1, KERNEL33);
        }

        if (sx != 1 || sy != 1) {
            pix.scaleGeneral(sx, sy, 0, 1);
        }
    }

    public static void preprocessCell(PixWrapper pix) throws PixError {
        pix.convertRGBToLuminance();
        pix.gammaTRC(0.2f, 0, 255);
        pix.invert();
    }
}
