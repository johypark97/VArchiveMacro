package com.github.johypark97.varchivemacro.macro.gui.presenter;

import java.nio.file.Path;
import java.util.Set;

public interface MacroViewConfig {
    Path getAccountPath();

    void setAccountPath(Path path);

    Path getCacheDir();

    void setCacheDir(Path path);

    Set<String> getSelectedDlcTabs();

    void setSelectedDlcTabs(Set<String> tabs);
}
