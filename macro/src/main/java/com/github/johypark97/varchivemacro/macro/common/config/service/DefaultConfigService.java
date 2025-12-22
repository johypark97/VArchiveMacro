package com.github.johypark97.varchivemacro.macro.common.config.service;

import com.github.johypark97.varchivemacro.macro.common.config.model.ConfigEditorModel;
import com.github.johypark97.varchivemacro.macro.common.config.repository.ConfigRepository;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultConfigService<C extends Record & ConfigEditorModel.Config<C, E>, E extends ConfigEditorModel.Editor<C, E>>
        implements ConfigService<C, E> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConfigService.class);

    private final ConfigRepository<C> configRepository;
    private final Supplier<C> defaultConfigSupplier;

    public DefaultConfigService(ConfigRepository<C> configRepository,
            Supplier<C> defaultConfigSupplier) {
        this.configRepository = Objects.requireNonNull(configRepository);
        this.defaultConfigSupplier = Objects.requireNonNull(defaultConfigSupplier);
    }

    @Override
    public C getConfig() {
        return configRepository.find();
    }

    @Override
    public void setConfig(C config) {
        configRepository.save(config);
    }

    @Override
    public void editConfig(UnaryOperator<E> editFunction) {
        configRepository.update(config -> editFunction.apply(config.edit()).commit());
    }

    @Override
    public void load() throws IOException {
        try {
            configRepository.refresh();
        } catch (NoSuchFileException e) {
            LOGGER.info("Config file not found. Using default configuration.");
            configRepository.save(defaultConfigSupplier.get());
        } catch (IOException e) {
            configRepository.save(defaultConfigSupplier.get());
            throw e;
        }
    }

    @Override
    public void save() throws IOException {
        configRepository.flush();
    }
}
