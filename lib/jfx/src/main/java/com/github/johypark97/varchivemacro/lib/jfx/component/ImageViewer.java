package com.github.johypark97.varchivemacro.lib.jfx.component;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class ImageViewer extends Pane {
    private static final double ZOOM_FACTOR = 0.005;

    private static final double ZOOM_LIMIT_MAX = 16;
    private static final double ZOOM_LIMIT_MIN = 0.0625; // 1 / 16

    private final ImageView imageView = new ImageView();
    private final PointDelta pointDelta = new PointDelta();

    private boolean flagScaleToFit;
    private double viewportCenterX;
    private double viewportCenterY;
    private double viewportZoom;

    public ImageViewer() {
        getChildren().add(imageView);

        imageView.fitWidthProperty().bind(widthProperty());
        imageView.fitHeightProperty().bind(heightProperty());

        layoutBoundsProperty().addListener((observable, oldValue, newValue) -> updateViewport());

        setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                pointDelta.update(event.getX(), event.getY());
            }

            if (event.isSecondaryButtonDown()) {
                scaleToFit();
            }
        });

        setOnMouseDragged(event -> {
            if (!event.isPrimaryButtonDown()) {
                return;
            }

            flagScaleToFit = false;

            pointDelta.update(event.getX(), event.getY());
            changeViewportCenterX(pointDelta.deltaX);
            changeViewportCenterY(pointDelta.deltaY);
            updateViewport();
        });

        setOnScroll(event -> {
            flagScaleToFit = false;

            changeZoom(event.getDeltaY());
            updateViewport();
        });
    }

    public void setImage(Image value) {
        imageView.setImage(value);

        flagScaleToFit = true;

        updateViewport();
    }

    public void clearImage() {
        imageView.setImage(null);
    }

    public void scaleToFit() {
        flagScaleToFit = true;

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

    private void changeViewportCenterX(double value) {
        double x = viewportCenterX - value / viewportZoom;
        viewportCenterX = Math.min(Math.max(0, x), getImageWidth());
    }

    private void changeViewportCenterY(double value) {
        double x = viewportCenterY - value / viewportZoom;
        viewportCenterY = Math.min(Math.max(0, x), getImageHeight());
    }

    private void setZoom(double value) {
        viewportZoom = Math.min(Math.max(ZOOM_LIMIT_MIN, value), ZOOM_LIMIT_MAX);
    }

    private void changeZoom(double value) {
        setZoom(viewportZoom * (value * ZOOM_FACTOR + 1));
    }

    private void updateViewport() {
        if (flagScaleToFit) {
            viewportCenterX = getImageWidth() / 2;
            viewportCenterY = getImageHeight() / 2;

            double zoomFitToWidth = getWidth() / getImageWidth();
            double zoomFitToHeight = getHeight() / getImageHeight();
            setZoom(Math.min(zoomFitToWidth, zoomFitToHeight));
        }

        double width = getWidth() / viewportZoom;
        double height = getHeight() / viewportZoom;

        double x = viewportCenterX - width / 2;
        double y = viewportCenterY - height / 2;

        Rectangle2D viewport = new Rectangle2D(x, y, width, height);
        imageView.setViewport(viewport);
    }

    public static class PointDelta {
        private double referenceX;
        private double referenceY;

        public double deltaX;
        public double deltaY;

        public void update(double x, double y) {
            deltaX = x - referenceX;
            deltaY = y - referenceY;

            referenceX = x;
            referenceY = y;
        }
    }
}
