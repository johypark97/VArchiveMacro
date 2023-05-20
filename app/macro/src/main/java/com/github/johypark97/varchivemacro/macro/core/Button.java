package com.github.johypark97.varchivemacro.macro.core;

import com.github.johypark97.varchivemacro.lib.common.api.Api;
import com.github.johypark97.varchivemacro.macro.core.scanner.collection.CollectionArea;
import com.google.common.collect.BiMap;
import com.google.common.collect.EnumBiMap;
import com.google.common.collect.ImmutableBiMap;

public enum Button {
    _4(0, 4), _5(1, 5), _6(2, 6), _8(3, 8);

    private final int value;
    private final int weight;

    Button(int w, int v) {
        value = v;
        weight = w;
    }

    public int getValue() {
        return value;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public Api.Button toApi() {
        return Converter.API_MAP.get(this);
    }

    public CollectionArea.Button toCollectionArea() {
        return Converter.COLLECTION_AREA_MAP.get(this);
    }

    public static Button valueOf(Api.Button button) {
        return Converter.API_MAP.inverse().get(button);
    }

    public static Button valueOf(CollectionArea.Button button) {
        return Converter.COLLECTION_AREA_MAP.inverse().get(button);
    }

    public static class Converter {
        public static final ImmutableBiMap<Button, Api.Button> API_MAP;
        public static final ImmutableBiMap<Button, CollectionArea.Button> COLLECTION_AREA_MAP;

        static {
            BiMap<Button, Api.Button> apiMap = EnumBiMap.create(Button.class, Api.Button.class);
            apiMap.put(_4, Api.Button._4);
            apiMap.put(_5, Api.Button._5);
            apiMap.put(_6, Api.Button._6);
            apiMap.put(_8, Api.Button._8);
            API_MAP = ImmutableBiMap.copyOf(apiMap);

            BiMap<Button, CollectionArea.Button> collectionAreaMap =
                    EnumBiMap.create(Button.class, CollectionArea.Button.class);
            collectionAreaMap.put(_4, CollectionArea.Button._4);
            collectionAreaMap.put(_5, CollectionArea.Button._5);
            collectionAreaMap.put(_6, CollectionArea.Button._6);
            collectionAreaMap.put(_8, CollectionArea.Button._8);
            COLLECTION_AREA_MAP = ImmutableBiMap.copyOf(collectionAreaMap);
        }
    }
}
