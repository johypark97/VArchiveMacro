package com.github.johypark97.varchivemacro.libjfx;

import java.util.Objects;
import javafx.concurrent.Service;

public class ServiceManagerHelper {
    public static boolean stopService(Class<? extends Service<?>> cls) {
        return Objects.requireNonNull(ServiceManager.getInstance().get(cls)).cancel();
    }
}
