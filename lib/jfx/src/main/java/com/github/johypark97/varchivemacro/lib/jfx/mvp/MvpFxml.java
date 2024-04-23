package com.github.johypark97.varchivemacro.lib.jfx.mvp;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class MvpFxml {
    public static void loadRoot(Parent root, URL url) {
        loadRoot(root, url, null);
    }

    public static void loadRoot(Parent root, URL url, ResourceBundle resource) {
        Objects.requireNonNull(root);
        Objects.requireNonNull(url);

        FXMLLoader fxmlLoader = new FXMLLoader(url);
        fxmlLoader.setController(root);
        fxmlLoader.setResources(resource);
        fxmlLoader.setRoot(root);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
