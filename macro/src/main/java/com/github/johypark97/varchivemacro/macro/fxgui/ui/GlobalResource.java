package com.github.johypark97.varchivemacro.macro.fxgui.ui;

import java.net.URL;
import java.util.Objects;

public class GlobalResource {
    private static final String ICON_PATH = "/images/overMeElFail.png";

    private static final String GLOBAL_CSS_FILE_NAME = "/fxml/styles/global.css";
    private static final String TABLE_COLOR_CSS_FILE_NAME = "/fxml/styles/table-color.css";
    private static final String TAB_COLOR_CSS_FILE_NAME = "/fxml/styles/tab-color.css";

    public static URL getIcon() {
        URL url = GlobalResource.class.getResource(ICON_PATH);
        return Objects.requireNonNull(url);
    }

    public static URL getGlobalCss() {
        URL url = GlobalResource.class.getResource(GLOBAL_CSS_FILE_NAME);
        return Objects.requireNonNull(url);
    }

    public static URL getTabColorCss() {
        URL url = GlobalResource.class.getResource(TAB_COLOR_CSS_FILE_NAME);
        return Objects.requireNonNull(url);
    }

    public static URL getTableColorCss() {
        URL url = GlobalResource.class.getResource(TABLE_COLOR_CSS_FILE_NAME);
        return Objects.requireNonNull(url);
    }
}
