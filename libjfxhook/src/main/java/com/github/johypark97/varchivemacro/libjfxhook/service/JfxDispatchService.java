package com.github.johypark97.varchivemacro.libjfxhook.service;

import com.github.kwhat.jnativehook.dispatcher.SwingDispatchService;
import javafx.application.Platform;

public class JfxDispatchService extends SwingDispatchService {
    @Override
    public void execute(Runnable r) {
        Platform.runLater(r);
    }
}
