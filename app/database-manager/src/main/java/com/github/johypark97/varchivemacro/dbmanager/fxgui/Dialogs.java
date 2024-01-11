package com.github.johypark97.varchivemacro.dbmanager.fxgui;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;

public final class Dialogs {
    public static void showException(Throwable throwable) {
        Alert alert = new Alert(AlertType.ERROR);

        alert.setTitle("Exception");
        alert.setHeaderText("Exception has been thrown.");
        alert.setContentText(throwable.toString());

        String stackText = Arrays.stream(throwable.getStackTrace()).map(StackTraceElement::toString)
                .collect(Collectors.joining(System.lineSeparator()));
        alert.getDialogPane().setExpandableContent(new TextArea(stackText));

        String style = new StyleBuilder().useFontMonospaced().useFontSize16px().build();
        alert.getDialogPane().setStyle(style);

        alert.showAndWait();
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
