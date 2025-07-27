package com.github.johypark97.varchivemacro.macro.common.programdata.app;

import java.nio.file.Path;
import java.util.Optional;

@FunctionalInterface
public interface UpdateProgressHook {
    void accept(int currentStep, int maxStep, Optional<Path> workingFilePath);
}
