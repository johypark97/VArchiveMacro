package com.github.johypark97.varchivemacro.macro.core.scanner.api.record;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public enum Button {
    B4(4),
    B5(5),
    B6(6),
    B8(8);

    private final int value;

    Button(int value) {
        this.value = value;
    }

    public int toInt() {
        return value;
    }

    public static class GsonSerializer implements JsonSerializer<Button> {
        @Override
        public JsonElement serialize(Button src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toInt());
        }
    }

    public static class GsonDeserializer implements JsonDeserializer<Button> {
        @Override
        public Button deserialize(JsonElement json,
                                  Type typeOfT,
                                  JsonDeserializationContext context) throws JsonParseException {
            return switch (json.getAsInt()) {
                case 4 -> Button.B4;
                case 5 -> Button.B5;
                case 6 -> Button.B6;
                case 8 -> Button.B8;
                default -> throw new JsonParseException("Invalid button value: " + json.getAsInt());
            };
        }
    }
}
