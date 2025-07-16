package com.github.johypark97.varchivemacro.macro.ui.resource;

import java.net.URL;
import java.util.Objects;

public enum UiResource {
    // icon
    ICON("/images/overMeElFail.png"),

    // css
    GLOBAL_CSS("/styles/global.css"),
    MODE_SELECTOR_CSS("/styles/mode-selector.css"),
    TABLE_COLOR_CSS("/styles/table-color.css"),
    TAB_COLOR_CSS("/styles/tab-color.css");

    private final String path;

    UiResource(String path) {
        this.path = path;
    }

    public URL url() {
        return Objects.requireNonNull(UiResource.class.getResource(path));
    }
}
