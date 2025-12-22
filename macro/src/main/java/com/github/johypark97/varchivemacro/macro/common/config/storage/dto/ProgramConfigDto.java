package com.github.johypark97.varchivemacro.macro.common.config.storage.dto;

import com.github.johypark97.varchivemacro.macro.common.config.model.ProgramConfig;
import com.google.gson.annotations.Expose;

public class ProgramConfigDto {
    @Expose
    public boolean prereleaseNotification;

    public static ProgramConfigDto fromModel(ProgramConfig model) {
        ProgramConfigDto dto = new ProgramConfigDto();

        dto.prereleaseNotification = model.prereleaseNotification().value();

        return dto;
    }

    public ProgramConfig toModel() {
        return ProgramConfig.editDefault().setPrereleaseNotification(prereleaseNotification)
                .commit();
    }
}
