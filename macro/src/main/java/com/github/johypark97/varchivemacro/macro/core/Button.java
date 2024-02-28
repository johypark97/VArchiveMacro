package com.github.johypark97.varchivemacro.macro.core;

import com.github.johypark97.varchivemacro.lib.common.Enums;
import com.google.common.base.Converter;

public enum Button {
    _4(0, 4), _5(1, 5), _6(2, 6), _8(3, 8);

    private static final ButtonConverter converter = new ButtonConverter();

    private final int integer;
    private final int weight;

    Button(int w, int i) {
        integer = i;
        weight = w;
    }

    public static Button valueOf(Enums.Button button) {
        return converter.reverse().convert(button);
    }

    public int getWeight() {
        return weight;
    }

    public Enums.Button toLib() {
        return converter.convert(this);
    }

    public int toInt() {
        return integer;
    }

    @Override
    public String toString() {
        return String.valueOf(integer);
    }

    public static class ButtonConverter extends Converter<Button, Enums.Button> {
        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        protected Enums.Button doForward(Button button) {
            return switch (button) {
                case _4 -> Enums.Button._4;
                case _5 -> Enums.Button._5;
                case _6 -> Enums.Button._6;
                case _8 -> Enums.Button._8;
            };
        }

        @Override
        protected Button doBackward(Enums.Button button) {
            return switch (button) {
                case _4 -> _4;
                case _5 -> _5;
                case _6 -> _6;
                case _8 -> _8;
            };
        }

        @Override
        public boolean equals(Object object) {
            return super.equals(object);
        }
    }
}
