package com.github.johypark97.varchivemacro.macro.core;

import com.github.johypark97.varchivemacro.lib.common.api.Api;
import com.github.johypark97.varchivemacro.macro.core.scanner.collection.CollectionArea;
import com.google.common.collect.BiMap;
import com.google.common.collect.EnumBiMap;
import com.google.common.collect.ImmutableBiMap;

public enum Pattern {
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

    public Api.Pattern toApi() {
        return Converter.API_MAP.get(this);
    }

    public CollectionArea.Pattern toCollectionArea() {
        return Converter.COLLECTION_AREA_MAP.get(this);
    }

    public static Pattern valueOf(Api.Pattern pattern) {
        return Converter.API_MAP.inverse().get(pattern);
    }

    public static Pattern valueOf(CollectionArea.Pattern pattern) {
        return Converter.COLLECTION_AREA_MAP.inverse().get(pattern);
    }

    public static class Converter {
        public static final ImmutableBiMap<Pattern, Api.Pattern> API_MAP;
        public static final ImmutableBiMap<Pattern, CollectionArea.Pattern> COLLECTION_AREA_MAP;

        static {
            BiMap<Pattern, Api.Pattern> apiMap = EnumBiMap.create(Pattern.class, Api.Pattern.class);
            apiMap.put(NM, Api.Pattern.NM);
            apiMap.put(HD, Api.Pattern.HD);
            apiMap.put(MX, Api.Pattern.MX);
            apiMap.put(SC, Api.Pattern.SC);
            API_MAP = ImmutableBiMap.copyOf(apiMap);

            BiMap<Pattern, CollectionArea.Pattern> collectionAreaMap =
                    EnumBiMap.create(Pattern.class, CollectionArea.Pattern.class);
            collectionAreaMap.put(NM, CollectionArea.Pattern.NM);
            collectionAreaMap.put(HD, CollectionArea.Pattern.HD);
            collectionAreaMap.put(MX, CollectionArea.Pattern.MX);
            collectionAreaMap.put(SC, CollectionArea.Pattern.SC);
            COLLECTION_AREA_MAP = ImmutableBiMap.copyOf(collectionAreaMap);
        }
    }
}
