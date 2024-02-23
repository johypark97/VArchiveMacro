package com.github.johypark97.varchivemacro.dbmanager.fxgui.model;

import com.github.johypark97.varchivemacro.dbmanager.fxgui.presenter.LiveTester.StartData;
import com.github.johypark97.varchivemacro.lib.common.area.NotSupportedResolutionException;
import com.github.johypark97.varchivemacro.lib.common.database.DlcSongManager.LocalDlcSong;
import com.github.johypark97.varchivemacro.lib.common.database.TitleTool;
import com.github.johypark97.varchivemacro.lib.common.ocr.OcrInitializationError;
import com.github.johypark97.varchivemacro.lib.common.ocr.PixError;
import java.awt.AWTException;
import java.io.IOException;
import java.util.List;
import javafx.scene.image.Image;

public interface LiveTesterModel {
    void initialize(List<LocalDlcSong> dlcSongList, TitleTool titleTool, StartData data)
            throws AWTException, NotSupportedResolutionException, OcrInitializationError;

    void terminate();

    RecognizedData recognize() throws IOException, PixError;

    class RecognizedData {
        public Image image;
        public String recognized;
        public String text;
    }
}
