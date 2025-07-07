package com.github.johypark97.varchivemacro.lib.jfx.component;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class ImageDisplay extends Pane {
    private static final double ZOOM_LIMIT_MIN = 0.0625; // 1 / 16

    private final ImageView imageView = new ImageView();

    public ImageDisplay() {
        getChildren().add(imageView);

        imageView.fitWidthProperty().bind(widthProperty());
        imageView.fitHeightProperty().bind(heightProperty());

        layoutBoundsProperty().addListener((observable, oldValue, newValue) -> updateViewport());
    }

    public void setImage(Image value) {
        imageView.setImage(value);

        updateViewport();
    }

    private double getImageWidth() {
        Image image = imageView.getImage();
        return (image != null) ? image.getWidth() : 0;
    }

    private double getImageHeight() {
        Image image = imageView.getImage();
        return (image != null) ? image.getHeight() : 0;
    }

    private void updateViewport() {
        double viewportCenterX = getImageWidth() / 2;
        double viewportCenterY = getImageHeight() / 2;

        double zoomFitToWidth = getWidth() / getImageWidth();
        double zoomFitToHeight = getHeight() / getImageHeight();

        double viewportZoom = Math.max(ZOOM_LIMIT_MIN, Math.min(zoomFitToWidth, zoomFitToHeight));

        double width = getWidth() / viewportZoom;
        double height = getHeight() / viewportZoom;

        double x = viewportCenterX - width / 2;
        double y = viewportCenterY - height / 2;

        Rectangle2D viewport = new Rectangle2D(x, y, width, height);
        imageView.setViewport(viewport);
    }
}
