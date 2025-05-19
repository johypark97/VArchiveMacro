package com.github.johypark97.varchivemacro.macro.ui.utility;

import java.util.function.Consumer;
import javafx.animation.Transition;
import javafx.util.Duration;

public class SimpleTransition extends Transition {
    public final Consumer<Double> consumer;

    public SimpleTransition(Duration duration, Consumer<Double> consumer) {
        this(duration, consumer, true);
    }

    public SimpleTransition(Duration duration, Consumer<Double> consumer, boolean initialize) {
        this.consumer = consumer;

        setCycleDuration(duration);

        if (initialize) {
            interpolate(0);
        }
    }

    @Override
    protected final void interpolate(double frac) {
        consumer.accept(frac);
    }
}
