package com.github.johypark97.varchivemacro.macro.common.config.service;

import com.github.johypark97.varchivemacro.macro.common.config.model.ConfigEditorModel;
import java.io.IOException;
import java.util.function.UnaryOperator;

public interface ConfigService<C extends Record & ConfigEditorModel.Config<C, E>, E extends ConfigEditorModel.Editor<C, E>> {
    C getConfig();

    void setConfig(C config);

    void editConfig(UnaryOperator<E> editFunction);

    void load() throws IOException;

    void save() throws IOException;
}
