package com.github.johypark97.varchivemacro.dbmanager.gui.presenter;

import com.github.johypark97.varchivemacro.dbmanager.gui.presenter.datastruct.LiveTesterConfig;
import com.github.johypark97.varchivemacro.lib.common.area.NotSupportedResolutionException;
import com.github.johypark97.varchivemacro.lib.common.ocr.OcrInitializationError;
import java.awt.AWTException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.JFrame;

public interface ILiveTester {
    interface Presenter {
        void linkView(View view);

        void start(JFrame parent, LiveTesterConfig config)
                throws OcrInitializationError, NotSupportedResolutionException, AWTException,
                IOException;

        void stop();

        void viewOpened();

        void viewClosing();

        void viewClosed();
    }


    interface View {
        void onLinkView(Presenter presenter);

        boolean isActive();

        void resetView();

        void showView(JFrame parent);

        void disposeView();

        void showResult(BufferedImage image, String text);
    }
}
