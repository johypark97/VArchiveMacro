package com.github.johypark97.varchivemacro.libjfxhook.domain.event;

@FunctionalInterface
public interface JfxHookEventSubscription {
    void unsubscribe();
}
