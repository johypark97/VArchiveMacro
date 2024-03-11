package com.github.johypark97.varchivemacro.dbmanager.fxgui.model;

import com.github.johypark97.varchivemacro.lib.jfx.ServiceManager;
import javafx.concurrent.Service;

public class ModelHelper {
    public static boolean stopService(Class<? extends Service<?>> cls) {
        Service<?> service = ServiceManager.getInstance().get(cls);
        if (service == null || !service.isRunning()) {
            return false;
        }

        service.cancel();

        return true;
    }
}
