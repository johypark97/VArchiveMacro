package com.github.johypark97.varchivemacro.macro.ui.event;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

public enum UiEventBus {
    INSTANCE; // Singleton

    private final Subject<UiEvent> subject = PublishSubject.create();

    public void fire(UiEvent uiEvent) {
        subject.onNext(uiEvent);
    }

    public Disposable subscribe(Consumer<UiEvent> onNext) {
        return subject.subscribe(onNext);
    }
}
