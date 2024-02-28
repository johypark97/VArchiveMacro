package com.github.johypark97.varchivemacro.dbmanager.fxgui;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;

public final class Dialogs {
    private static final String STACK_PREFIX = "\tat ";

    public static void showException(Throwable throwable) {
        Alert alert = new Alert(AlertType.ERROR);

        alert.setTitle("Exception");
        alert.setHeaderText("Exception has been thrown.");
        alert.setContentText(throwable.toString());

        StringBuilder builder = new StringBuilder();
        Throwable pThrowable = throwable;
        do {
            String stack = Arrays.stream(pThrowable.getStackTrace()).map(x -> STACK_PREFIX + x)
                    .collect(Collectors.joining(System.lineSeparator()));

            builder.append(pThrowable).append(System.lineSeparator());
            builder.append(stack).append(System.lineSeparator());

            pThrowable = pThrowable.getCause();
        } while (pThrowable != null);
        alert.getDialogPane().setExpandableContent(new TextArea(builder.toString()));

        String style = new StyleBuilder().useFontMonospaced().useFontSize16px().build();
        alert.getDialogPane().setStyle(style);

        alert.showAndWait();
    }

    public static void showAlert(String content, String header, String title, AlertType type) {
        Alert alert = new Alert(type);

        if (title != null) {
            alert.setTitle(title);
        }

        if (header != null) {
            alert.setHeaderText(header);
        }

        if (content != null) {
            alert.setContentText(content);
        }

        String style = new StyleBuilder().useFontSansSerif().useFontSize16px().build();
        alert.getDialogPane().setStyle(style);

        alert.showAndWait();
    }

    public static void showInformation(String content, String header, String title) {
        showAlert(content, header, title, AlertType.INFORMATION);
    }

    public static void showInformation(String content, String header) {
        showInformation(content, header, null);
    }

    public static void showInformation(String content) {
        showInformation(content, null);
    }

    public static void showWarning(String content, String header, String title) {
        showAlert(content, header, title, AlertType.WARNING);
    }

    public static void showWarning(String content, String header) {
        showWarning(content, header, null);
    }

    public static void showWarning(String content) {
        showWarning(content, null);
    }

    public static class StyleBuilder {
        public static final String FONT_FAMILY_MONOSPACED = "-fx-font-family: Monospaced;";
        public static final String FONT_FAMILY_SANS_SERIF = "-fx-font-family: SansSerif;";
        public static final String FONT_SIZE_16PX = "-fx-font-size: 16px;";

        private final Set<String> selectedList = new HashSet<>();

        public String build() {
            return String.join("", selectedList);
        }

        public StyleBuilder useFontSize16px() {
            selectedList.add(FONT_SIZE_16PX);
            return this;
        }

        public StyleBuilder useFontMonospaced() {
            selectedList.add(FONT_FAMILY_MONOSPACED);
            return this;
        }

        public StyleBuilder useFontSansSerif() {
            selectedList.add(FONT_FAMILY_SANS_SERIF);
            return this;
        }
    }
}
