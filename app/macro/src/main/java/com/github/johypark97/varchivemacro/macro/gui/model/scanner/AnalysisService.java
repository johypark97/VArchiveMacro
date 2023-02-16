package com.github.johypark97.varchivemacro.macro.gui.model.scanner;

import com.github.johypark97.varchivemacro.lib.common.image.ImageConverter;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.CollectionTaskData.RecordData;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.collection.CollectionArea;
import com.github.johypark97.varchivemacro.macro.gui.model.scanner.collection.CollectionAreaFactory;
import com.github.johypark97.varchivemacro.macro.ocr.OcrWrapper;
import com.github.johypark97.varchivemacro.macro.ocr.PixWrapper;
import com.google.common.base.CharMatcher;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnalysisService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisService.class);

    public static final int COMBO_MARK_THRESHOLD = 192;

    public static CollectionTaskData analyze(CaptureTask task) {
        CollectionTaskData data = new CollectionTaskData();

        byte[] bytes = task.getImageBytes();
        if (bytes == null) {
            Exception e = new NullPointerException("image bytes is null");
            LOGGER.atDebug().addKeyValue("taskNumber", task.taskNumber)
                    .addKeyValue("title", task.getSongTitle()).addArgument(e).log(e.getMessage());
            task.setException(e);
            return null;
        }

        LOGGER.atDebug().log(task.getSongTitle());

        try (OcrWrapper ocr = new OcrWrapper(); PixWrapper pix = new PixWrapper(bytes)) {
            ocr.setWhitelist("0123456789.%");

            Dimension size = new Dimension(pix.getWidth(), pix.getHeight());
            CollectionArea area = CollectionAreaFactory.create(size);

            // -------- preprocessing --------
            pix.convertRGBToLuminance();
            pix.gammaTRC(0.2f, 0, 255);
            pix.invert();

            // store full image
            data.setFullImage(pix.getPngBytes());

            // store title image
            try (PixWrapper titlePix = pix.crop(area.getTitle())) {
                data.setTitleImage(titlePix.getPngBytes());
            }

            // -------- analyze records --------
            for (Entry<String, Rectangle> entry : area.getRateMap().entrySet()) {
                try (PixWrapper recordPix = pix.crop(entry.getValue())) {

                    String text = ocr.run(recordPix.pixInstance);
                    text = CharMatcher.whitespace().removeFrom(text);

                    LOGGER.atDebug().log(text);
                    // text = parseRateText(text);

                    Image image = ImageConverter.pngBytesToImage(recordPix.getPngBytes());
                    data.addRecord(entry.getKey(), image, text);
                }
            }

            area.getComboMarkMap(ImageConverter.pngBytesToImage(task.getImageBytes()))
                    .forEach((key, value) -> {
                        RecordData recordData = data.records.get(key);
                        recordData.maxCombo = isMaxCombo(value);

                        binarizeComboMarkImage(value);
                        recordData.maxComboImage = value;
                    });
            return data;
        } catch (Exception e) {
            LOGGER.atError().log(e.getMessage(), e);
            return null;
        }
    }

    public static String parseRateText(String text) {
        int index = text.indexOf('%');
        if (index == -1) {
            return "";
        }

        try {
            String s = text.substring(0, index);
            float value = Float.parseFloat(s);
            return (value <= 100) ? String.valueOf(value) : "";
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            return "";
        }
    }

    public static void binarizeComboMarkImage(BufferedImage image) {
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                Color c = isMaxCombo(image.getRGB(x, y)) ? Color.BLACK : Color.WHITE;
                image.setRGB(x, y, c.getRGB());
            }
        }
    }

    public static boolean isMaxCombo(BufferedImage image) {
        int x = image.getWidth() / 2;
        int y = image.getHeight() / 2;
        return isMaxCombo(image.getRGB(x, y));
    }

    public static boolean isMaxCombo(int rgb) {
        Color color = new Color(rgb);
        return color.getRed() >= COMBO_MARK_THRESHOLD || color.getGreen() >= COMBO_MARK_THRESHOLD
                || color.getBlue() >= COMBO_MARK_THRESHOLD;
    }
}
