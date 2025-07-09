package com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.app;

import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.domain.model.PngImage;
import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.domain.repository.CaptureImageRepository;
import java.io.IOException;

public class CaptureImageService implements CaptureImageRepository {
    private final CaptureImageRepository captureImageRepository;

    public CaptureImageService(CaptureImageRepository captureImageRepository) {
        this.captureImageRepository = captureImageRepository;
    }

    @Override
    public boolean isEmpty() throws IOException {
        return captureImageRepository.isEmpty();
    }

    @Override
    public void deleteAll() throws IOException {
        captureImageRepository.deleteAll();
    }

    @Override
    public void save(int id, PngImage pngImage) throws IOException {
        captureImageRepository.save(id, pngImage);
    }

    @Override
    public PngImage findById(int id) throws IOException {
        return captureImageRepository.findById(id);
    }
}
