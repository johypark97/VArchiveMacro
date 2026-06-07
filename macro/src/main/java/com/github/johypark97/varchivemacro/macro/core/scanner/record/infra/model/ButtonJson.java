package com.github.johypark97.varchivemacro.macro.core.scanner.record.infra.model;

import com.github.johypark97.varchivemacro.macro.core.scanner.record.domain.model.RecordButton;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public enum ButtonJson {
    B4(RecordButton.B4, 4),
    B5(RecordButton.B5, 5),
    B6(RecordButton.B6, 6),
    B8(RecordButton.B8, 8);

    private final RecordButton domainButton;
    private final int value;

    ButtonJson(RecordButton domainButton, int value) {
        this.domainButton = domainButton;
        this.value = value;
    }

    public static ButtonJson fromDomain(RecordButton button) {
        return switch (button) {
            case B4 -> B4;
            case B5 -> B5;
            case B6 -> B6;
            case B8 -> B8;
        };
    }

    public RecordButton toDomain() {
        return domainButton;
    }

    public int toInt() {
        return value;
    }

    public static class GsonSerializer implements JsonSerializer<ButtonJson> {
        @Override
        public JsonElement serialize(
                ButtonJson src,
                Type typeOfSrc,
                JsonSerializationContext context
        ) {
            return new JsonPrimitive(src.toInt());
        }
    }

    public static class GsonDeserializer implements JsonDeserializer<ButtonJson> {
        @Override
        public ButtonJson deserialize(
                JsonElement json,
                Type typeOfT,
                JsonDeserializationContext context
        ) throws JsonParseException {
            return switch (json.getAsInt()) {
                case 4 -> ButtonJson.B4;
                case 5 -> ButtonJson.B5;
                case 6 -> ButtonJson.B6;
                case 8 -> ButtonJson.B8;
                default -> throw new JsonParseException("Invalid button value: " + json.getAsInt());
            };
        }
    }
}
