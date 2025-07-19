package com.github.johypark97.varchivemacro.macro.ui.common;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventDebouncer {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventDebouncer.class);

    private final BooleanProperty eventTrigger = new SimpleBooleanProperty();

    private Runnable callback;

    public EventDebouncer() {
        eventTrigger.addListener((observable, oldValue, newValue) -> {
            if (!oldValue && newValue) {
                Platform.runLater(() -> eventTrigger.set(false));
            } else if (oldValue && !newValue && callback != null) {
                LOGGER.atTrace().log("Run callback");
                callback.run();
            }
        });
    }

    public void trigger() {
        eventTrigger.set(true);
    }

    public void setCallback(Runnable value) {
        callback = value;
    }
}
