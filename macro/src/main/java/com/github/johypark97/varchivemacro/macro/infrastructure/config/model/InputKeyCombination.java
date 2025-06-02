package com.github.johypark97.varchivemacro.macro.infrastructure.config.model;

import com.github.johypark97.varchivemacro.lib.desktop.InputKey;
import com.google.gson.annotations.Expose;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record InputKeyCombination(@Expose InputKey key, @Expose boolean ctrl, @Expose boolean alt,
                                  @Expose boolean shift) {
    public static InputKeyCombination from(InputKey key) {
        return new InputKeyCombination(key, false, false, false);
    }

    public List<InputKey> modifierList() {
        List<InputKey> list = new ArrayList<>(3);

        if (ctrl) {
            list.add(InputKey.CONTROL);
        }

        if (alt) {
            list.add(InputKey.ALT);
        }

        if (shift) {
            list.add(InputKey.SHIFT);
        }

        return list;
    }

    public int[] modifierKeyCodeArray() {
        return modifierList().stream().mapToInt(InputKey::toAwtKeyCode).toArray();
    }

    @Override
    public String toString() {
        String keyString;

        if (InputKey.PLUS.equals(key)) {
            keyString = "'+'";
        } else if (!key.toChar().isEmpty()) {
            keyString = key.toChar();
        } else {
            keyString = key.toString();
        }

        return Stream.concat(modifierList().stream().map(InputKey::toString), Stream.of(keyString))
                .collect(Collectors.joining(" + "));
    }
}
