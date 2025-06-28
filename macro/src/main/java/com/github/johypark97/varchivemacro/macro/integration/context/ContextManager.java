package com.github.johypark97.varchivemacro.macro.integration.context;

import com.github.johypark97.varchivemacro.lib.common.manager.InstanceManager;
import com.github.johypark97.varchivemacro.lib.common.manager.LazyInstanceManager;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.HostServices;

public enum ContextManager {
    INSTANCE; // Singleton

    private final InstanceManager<Context> instanceManager = new LazyInstanceManager<>();

    private final AtomicBoolean initialized = new AtomicBoolean();

    public synchronized void initialize(HostServices hostServices) {
        if (initialized.get()) {
            throw new IllegalStateException("ContextManager is already initialized.");
        }

        instanceManager.setInstance(GlobalContext.class, new GlobalContext(hostServices));

        initialized.set(true);
    }

    public GlobalContext getGlobalContext() {
        checkInitialization();

        return getInstance(GlobalContext.class);
    }

    public MacroContext createMacroContext() {
        return new MacroContext(getGlobalContext());
    }

    public OpenSourceLicenseContext createOpenSourceLicenseContext() {
        return new OpenSourceLicenseContext();
    }

    public ScannerContext createScannerContext() throws IOException {
        return new ScannerContext(getGlobalContext());
    }

    private <T extends Context> T getInstance(Class<T> cls) {
        checkInitialization();

        return instanceManager.getInstance(cls);
    }

    private void checkInitialization() {
        if (!initialized.get()) {
            throw new IllegalStateException("ContextManager is not initialized.");
        }
    }
}
