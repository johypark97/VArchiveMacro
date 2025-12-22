package com.github.johypark97.varchivemacro.macro.common.config.storage.dto;

import com.github.johypark97.varchivemacro.macro.common.config.model.AppConfig;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AppConfigDto {
    @Expose
    @SerializedName("macroConfig")
    public MacroConfigDto macroConfigDto;

    @Expose
    @SerializedName("scannerConfig")
    public ScannerConfigDto scannerConfigDto;

    @Expose
    @SerializedName("programConfig")
    public ProgramConfigDto programConfigDto;

    public static AppConfigDto fromModel(AppConfig model) {
        AppConfigDto dto = new AppConfigDto();

        dto.macroConfigDto = MacroConfigDto.fromModel(model.macroConfig());
        dto.scannerConfigDto = ScannerConfigDto.fromModel(model.scannerConfig());
        dto.programConfigDto = ProgramConfigDto.fromModel(model.programConfig());

        return dto;
    }

    public AppConfig toModel() {
        return AppConfig.editDefault().editMacroConfig(x -> macroConfigDto.toModel().edit())
                .editScannerConfig(x -> scannerConfigDto.toModel().edit())
                .editProgramConfig(x -> programConfigDto.toModel().edit()).commit();
    }
}
