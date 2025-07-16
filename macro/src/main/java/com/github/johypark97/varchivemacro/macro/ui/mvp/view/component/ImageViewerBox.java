package com.github.johypark97.varchivemacro.macro.ui.mvp.view.component;

import com.github.johypark97.varchivemacro.lib.jfx.component.ImageViewer;
import com.github.johypark97.varchivemacro.macro.common.i18n.Language;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ImageViewerBox extends VBox {
    private final ImageViewer imageViewer = new ImageViewer();

    public ImageViewerBox() {
        Language language = Language.INSTANCE;

        HBox box = new HBox();
        box.getChildren().add(new Label(language.getString("component.imageViewerBox.lmb")));
        box.getChildren().add(new Label(language.getString("component.imageViewerBox.mmb")));
        box.getChildren().add(new Label(language.getString("component.imageViewerBox.rmb")));
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10));
        box.setSpacing(20);
        getChildren().add(box);

        imageViewer.getStyleClass().add("image-viewer");
        imageViewer.setPrefHeight(0);
        imageViewer.setPrefWidth(0);
        getChildren().add(imageViewer);
        VBox.setVgrow(imageViewer, Priority.ALWAYS);
    }

    public void setImage(Image value) {
        imageViewer.setImage(value);
    }
}
