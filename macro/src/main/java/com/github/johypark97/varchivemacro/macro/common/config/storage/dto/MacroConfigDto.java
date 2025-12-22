package com.github.johypark97.varchivemacro.macro.common.config.storage.dto;

import com.github.johypark97.varchivemacro.macro.common.config.model.MacroConfig;
import com.google.gson.annotations.Expose;

public class MacroConfigDto {
    @Expose
    public ShortcutKey startDownKey;

    @Expose
    public ShortcutKey startUpKey;

    @Expose
    public ShortcutKey stopKey;

    @Expose
    public ShortcutKey uploadKey;

    @Expose
    public ClientMode clientMode;

    @Expose
    public Integer count;

    @Expose
    public Integer keyHoldTime;

    @Expose
    public Integer postCaptureDelay;

    @Expose
    public Integer songSwitchingTime;

    public static MacroConfigDto fromModel(MacroConfig model) {
        MacroConfigDto dto = new MacroConfigDto();

        dto.startDownKey = ShortcutKey.fromModel(model.startDownKey().value());
        dto.startUpKey = ShortcutKey.fromModel(model.startUpKey().value());
        dto.stopKey = ShortcutKey.fromModel(model.stopKey().value());
        dto.uploadKey = ShortcutKey.fromModel(model.uploadKey().value());
        dto.clientMode = ClientMode.fromModel(model.clientMode().value());
        dto.count = model.count().value();
        dto.keyHoldTime = model.keyHoldTime().value();
        dto.postCaptureDelay = model.postCaptureDelay().value();
        dto.songSwitchingTime = model.songSwitchingTime().value();

        return dto;
    }

    public MacroConfig toModel() {
        return MacroConfig.editDefault().setStartDownKey(startDownKey.toModel())
                .setStartUpKey(startUpKey.toModel()).setStopKey(stopKey.toModel())
                .setUploadKey(uploadKey.toModel()).setClientMode(clientMode.toModel())
                .setCount(count).setKeyHoldTime(keyHoldTime).setPostCaptureDelay(postCaptureDelay)
                .setSongSwitchingTime(songSwitchingTime).commit();
    }
}
