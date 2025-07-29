package com.github.johypark97.varchivemacro.macro.common.programdata.infra.model;

import com.github.johypark97.varchivemacro.macro.common.converter.ZonedDateTimeConverter;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.time.ZonedDateTime;

public class ZonedDateTimeJsonTypeAdapter
        implements JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {
    @Override
    public JsonElement serialize(ZonedDateTime zonedDateTime, Type type,
            JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(ZonedDateTimeConverter.format(zonedDateTime));
    }

    @Override
    public ZonedDateTime deserialize(JsonElement jsonElement, Type type,
            JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        try {
            return ZonedDateTimeConverter.parse(jsonElement.getAsString());
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
    }
}
