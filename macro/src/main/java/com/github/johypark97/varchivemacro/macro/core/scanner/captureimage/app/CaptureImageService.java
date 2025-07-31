package com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.app;

import com.github.johypark97.varchivemacro.macro.core.scanner.captureimage.domain.repository.CaptureImageRepository;
import java.awt.image.BufferedImage;
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
    public void save(int id, BufferedImage image) throws IOException {
        captureImageRepository.save(id, image);
    }

    @Override
    public BufferedImage findById(int id) throws IOException {
        return captureImageRepository.findById(id);
    }
}
