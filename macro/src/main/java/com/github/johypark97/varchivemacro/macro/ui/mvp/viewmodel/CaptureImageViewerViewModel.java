package com.github.johypark97.varchivemacro.macro.ui.mvp.viewmodel;

import com.github.johypark97.varchivemacro.macro.core.scanner.capture.domain.model.CaptureEntry;
import javafx.scene.image.Image;

public class CaptureImageViewerViewModel {
    public static class CaptureImageDetail {
        public final CellData[][] cellDataArray = new CellData[4][4];

        public Image captureImage;
        public Image titleImage;
        public String titleText;
        public boolean analyzed;


        public record CellData(Image rateImage, Image maxComboImage, String rateText,
                               boolean maxCombo) {
        }
    }


    public record CaptureImage(int entryId, String scannedTitle) {
        public static CaptureImage from(CaptureEntry captureEntry) {
            return new CaptureImage(captureEntry.entryId(), captureEntry.capture().scannedTitle);
        }
    }
}
