package com.github.johypark97.varchivemacro.macro.ui.event;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.subjects.PublishSubject;
import io.reactivex.rxjava3.subjects.Subject;

public enum GlobalEventBus {
    INSTANCE; // Singleton

    private final Subject<GlobalEvent> subject = PublishSubject.create();

    public void fire(GlobalEvent event) {
        subject.onNext(event);
    }

    public Disposable subscribe(Consumer<GlobalEvent> onNext) {
        return subject.subscribe(onNext);
    }
}
