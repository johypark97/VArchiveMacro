package com.github.johypark97.varchivemacro.macro.application.provider;

import com.github.johypark97.varchivemacro.lib.common.manager.InstanceManager;
import com.github.johypark97.varchivemacro.lib.common.manager.LazyInstanceManager;
import com.github.johypark97.varchivemacro.macro.application.service.WebBrowserService;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.HostServices;

public enum ServiceProvider {
    INSTANCE; // Singleton

    private final InstanceManager<Object> instanceManager = new LazyInstanceManager<>();

    private final AtomicBoolean initialized = new AtomicBoolean();

    public synchronized void initialize(HostServices hostServices) {
        if (initialized.get()) {
            throw new IllegalStateException("ServiceProvider is already initialized.");
        }

        instanceManager.setConstructor(WebBrowserService.class,
                () -> new WebBrowserService(hostServices));

        initialized.set(true);
    }

    public WebBrowserService getWebBrowserService() {
        return getInstance(WebBrowserService.class);
    }

    private <T> T getInstance(Class<T> cls) {
        checkInitialization();

        return instanceManager.getInstance(cls);
    }

    private void checkInitialization() {
        if (!initialized.get()) {
            throw new IllegalStateException("ServiceProvider is not initialized.");
        }
    }
}
