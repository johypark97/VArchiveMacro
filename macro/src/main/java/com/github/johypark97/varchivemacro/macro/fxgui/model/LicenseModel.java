package com.github.johypark97.varchivemacro.macro.fxgui.model;

import java.io.IOException;
import java.util.List;

public interface LicenseModel {
    List<String> getLibraryList();

    String getLicenseText(String library) throws IOException;

    String getLibraryUrl(String library);
}
