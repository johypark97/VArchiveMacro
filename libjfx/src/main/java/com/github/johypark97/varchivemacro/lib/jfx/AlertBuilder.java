package com.github.johypark97.varchivemacro.lib.jfx;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;
import javafx.stage.Window;

public class AlertBuilder {
    private static final String STACK_PREFIX = "\tat ";

    public final Alert alert;

    public AlertBuilder(AlertType type) {
        alert = new Alert(type);

        alert.setHeaderText(null);

        String style = new StyleBuilder().useFontSansSerif().useFontSize16px().build();
        alert.getDialogPane().setStyle(style);
    }

    public static AlertBuilder confirmation() {
        return new AlertBuilder(AlertType.CONFIRMATION);
    }

    public static AlertBuilder error() {
        return new AlertBuilder(AlertType.ERROR);
    }

    public static AlertBuilder information() {
        return new AlertBuilder(AlertType.INFORMATION);
    }

    public static AlertBuilder warning() {
        return new AlertBuilder(AlertType.WARNING);
    }

    public AlertBuilder setTitle(String value) {
        alert.setTitle(value);

        return this;
    }

    public AlertBuilder setHeaderText(String value) {
        alert.setHeaderText(value);

        return this;
    }

    public AlertBuilder setContentText(String value) {
        alert.setContentText(value);

        return this;
    }

    public AlertBuilder setThrowable(Throwable throwable) {
        StringBuilder builder = new StringBuilder();

        Throwable pThrowable = throwable;
        do {
            String stack = Arrays.stream(pThrowable.getStackTrace()).map(x -> STACK_PREFIX + x)
                    .collect(Collectors.joining(System.lineSeparator()));

            builder.append(pThrowable).append(System.lineSeparator()).append(stack)
                    .append(System.lineSeparator());

            pThrowable = pThrowable.getCause();
        } while (pThrowable != null);

        alert.getDialogPane().setExpandableContent(new TextArea(builder.toString()));

        return this;
    }

    public AlertBuilder setOwner(Window window) {
        alert.initOwner(window);

        return this;
    }

    public static class StyleBuilder {
        public static final String FONT_FAMILY_MONOSPACED = "-fx-font-family: Monospaced;";
        public static final String FONT_FAMILY_SANS_SERIF = "-fx-font-family: SansSerif;";
        public static final String FONT_SIZE_16PX = "-fx-font-size: 16px;";

        private final Set<String> selectedList = new HashSet<>();

        public String build() {
            return String.join(" ", selectedList);
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
