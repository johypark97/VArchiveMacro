package com.github.johypark97.varchivemacro.macro.ui.mvp;

import com.github.johypark97.varchivemacro.lib.jfx.Mvp;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;

public interface ScannerProcessorFrame {
    interface Presenter extends Mvp.MvpPresenter<View, Presenter> {
        void startView();

        void showCaptureImageViewer();

        <T extends Node & ViewButtonController> void setCenterView(T value);
    }


    interface View extends Mvp.MvpView<View, Presenter> {
        void setCenterNode(Node value);

        void setLeftButtonFunction(ButtonFunction value);

        void setRightButtonFunction(ButtonFunction value);

        void runLeftButtonAction();
    }


    interface ViewButtonController {
        ButtonFunction getLeftButtonFunction();

        ButtonFunction getRightButtonFunction();
    }


    record ButtonFunction(String text, EventHandler<ActionEvent> eventHandler) {
    }
}
