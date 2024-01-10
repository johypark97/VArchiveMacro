package com.github.johypark97.varchivemacro.lib.common.mvp;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class MvpFxml {
    public static void loadRoot(Parent root, URL url) {
        Objects.requireNonNull(root);
        Objects.requireNonNull(url);

        FXMLLoader fxmlLoader = new FXMLLoader(url);
        fxmlLoader.setController(root);
        fxmlLoader.setRoot(root);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
