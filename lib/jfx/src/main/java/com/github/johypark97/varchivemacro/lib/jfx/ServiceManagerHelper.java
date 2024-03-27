package com.github.johypark97.varchivemacro.lib.jfx;

import javafx.concurrent.Service;

public class ServiceManagerHelper {
    public static boolean stopService(Class<? extends Service<?>> cls) {
        Service<?> service = ServiceManager.getInstance().get(cls);
        if (service == null || !service.isRunning()) {
            return false;
        }

        service.cancel();

        return true;
    }
}
