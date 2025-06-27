package com.github.johypark97.varchivemacro.macro.common.validator;

import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.infra.repository.DiskCaptureImageRepository;
import java.io.IOException;
import java.nio.file.Path;

public class DiskCaptureImageCacheDirectoryValidator {
    public static void validate(Path cacheDirectoryPath) throws IOException {
        DiskCaptureImageRepository.validate(cacheDirectoryPath);
    }
}
