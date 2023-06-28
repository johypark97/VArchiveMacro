package com.github.johypark97.varchivemacro.lib.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

public interface Enums {
    enum Button {
        _4(0, 4), _5(1, 5), _6(2, 6), _8(3, 8);

        private final int integer;
        private final int weight;

        Button(int w, int i) {
            integer = i;
            weight = w;
        }

        public int getWeight() {
            return weight;
        }

        public int toInt() {
            return integer;
        }

        @Override
        public String toString() {
            return Integer.toString(integer);
        }

        public static class GsonSerializer implements JsonSerializer<Button> {
            @Override
            public JsonElement serialize(Button src, Type typeOfSrc,
                    JsonSerializationContext context) {
                return new JsonPrimitive(src.toInt());
            }
        }
    }


    enum Pattern {
        NM(0, "NORMAL", "NM"), HD(1, "HARD", "HD"), MX(2, "MAXIMUM", "MX"), SC(3, "SC", "SC");

        private final String fullName;
        private final String shortName;
        private final int weight;

        Pattern(int w, String f, String s) {
            fullName = f;
            shortName = s;
            weight = w;
        }

        public String getShortName() {
            return shortName;
        }

        public int getWeight() {
            return weight;
        }

        @Override
        public String toString() {
            return fullName;
        }
    }
}
