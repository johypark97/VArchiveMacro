package com.github.johypark97.varchivemacro.model;

import com.github.johypark97.varchivemacro.config.ConfigData;
import com.github.johypark97.varchivemacro.config.IConfigObserver;

public class MacroModel implements IConfigObserver {
    private MacroData data;

    public MacroData getData() {
        if (data == null) {
            synchronized (this) {
                if (data == null)
                    data = new MacroData();
            }
        }

        return data;
    }

    public synchronized void setData(MacroData data) {
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
