package com.github.johypark97.varchivemacro.gui.model;

import com.github.johypark97.varchivemacro.config.ConfigData;
import com.github.johypark97.varchivemacro.config.IConfigObserver;
import com.github.johypark97.varchivemacro.gui.model.datastruct.SettingsData;

public class SettingsModel implements IConfigObserver {
    private SettingsData data;

    public SettingsData getData() {
        if (data == null) {
            synchronized (this) {
                if (data == null)
                    data = new SettingsData();
            }
        }

        return data;
    }

    public synchronized void setData(SettingsData data) {
        this.data = data;
    }

    @Override
    public void configWillBeSaved(ConfigData configDataRef) {
        configDataRef.lastSettings = getData();
    }

    @Override
    public void configIsLoaded(ConfigData configDataRef) {
        setData(configDataRef.lastSettings);
    }
}
