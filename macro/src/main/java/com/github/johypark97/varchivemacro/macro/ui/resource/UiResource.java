package com.github.johypark97.varchivemacro.macro.ui.resource;

import java.net.URL;
import java.util.Objects;

public class UiResource {
    private static final String ICON_PATH = "/images/overMeElFail.png";

    private static final String GLOBAL_CSS_FILE_NAME = "/fxml/styles/global.css";
    private static final String TABLE_COLOR_CSS_FILE_NAME = "/fxml/styles/table-color.css";
    private static final String TAB_COLOR_CSS_FILE_NAME = "/fxml/styles/tab-color.css";

    public static URL getIcon() {
        URL url = UiResource.class.getResource(ICON_PATH);
        return Objects.requireNonNull(url);
    }

    public static URL getGlobalCss() {
        URL url = UiResource.class.getResource(GLOBAL_CSS_FILE_NAME);
        return Objects.requireNonNull(url);
    }

    public static URL getTabColorCss() {
        URL url = UiResource.class.getResource(TAB_COLOR_CSS_FILE_NAME);
        return Objects.requireNonNull(url);
    }

    public static URL getTableColorCss() {
        URL url = UiResource.class.getResource(TABLE_COLOR_CSS_FILE_NAME);
        return Objects.requireNonNull(url);
    }
}
