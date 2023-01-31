package com.github.johypark97.varchivemacro.lib.common.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public enum Button {
    _4(4), _5(5), _6(6), _8(8);

    private final int value;

    Button(int i) {
        value = i;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    public static class GsonSerializer implements JsonSerializer<Button> {
        @Override
        public JsonElement serialize(Button src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getValue());
        }
    }
}
