package com.github.johypark97.varchivemacro.macro.ui.resource;

import java.net.URL;
import java.util.Objects;

public enum UiResource {
    // icon
    ICON("/images/overMeElFail.png"),

    // css
    GLOBAL_CSS("/fxml/styles/global.css"),
    TABLE_COLOR_CSS("/fxml/styles/table-color.css"),
    TAB_COLOR_CSS("/fxml/styles/tab-color.css");

    private final String path;

    UiResource(String path) {
        this.path = path;
    }

    public URL url() {
        return Objects.requireNonNull(UiResource.class.getResource(path));
    }
}
