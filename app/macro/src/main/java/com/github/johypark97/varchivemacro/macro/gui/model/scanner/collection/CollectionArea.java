package com.github.johypark97.varchivemacro.macro.gui.model.scanner.collection;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface CollectionArea {
    enum Section {
        COUNT(0), SCORE(1), RATE(2), COMBO(3);

        private final int value;

        Section(int i) {
            value = i;
        }

        public int getValue() {
            return value;
        }
    }


    enum Button {
        _4(0), _5(1), _6(2), _8(3);

        private final int value;

        Button(int i) {
            value = i;
        }

        public int getValue() {
            return value;
        }
    }


    enum Pattern {
        NM(0), HD(1), MX(2), SC(3);

        private final int value;

        Pattern(int i) {
            value = i;
        }

        public int getValue() {
            return value;
        }
    }

    Rectangle getTitle();

    Rectangle getCell(Section section, Button button, Pattern pattern);

    Rectangle getRate(Button button, Pattern pattern);

    Rectangle getComboMark(Button button, Pattern pattern);

    default Map<String, List<Rectangle>> getRateComboMarkMap() {
        Map<String, List<Rectangle>> map = new HashMap<>();

        for (Button button : Button.values()) {
            for (Pattern pattern : Pattern.values()) {
                String key = generateKey(button, pattern);
                map.put(key, List.of(getRate(button, pattern), getComboMark(button, pattern)));
            }
        }

        return map;
    }

    default Map<String, List<BufferedImage>> getRateComboMarkMap(BufferedImage image) {
        return getCroppedImageListMap(image, this::getRateComboMarkMap);
    }

    default String generateKey(Button button, Pattern pattern) {
        // key format: _4B_NM
        return String.format("%sB_%s", button, pattern);
    }

    default Map<String, BufferedImage> getCroppedImageMap(BufferedImage image,
            Supplier<Map<String, Rectangle>> supplier) {
        return supplier.get().entrySet().stream()
                .collect(Collectors.toMap(Entry::getKey, (entry) -> {
                    Rectangle r = entry.getValue();
                    return image.getSubimage(r.x, r.y, r.width, r.height);
                }));
    }

    default Map<String, List<BufferedImage>> getCroppedImageListMap(BufferedImage image,
            Supplier<Map<String, List<Rectangle>>> supplier) {
        return supplier.get().entrySet().stream().collect(Collectors.toMap(Entry::getKey,
                (entry) -> entry.getValue().stream()
                        .map((r) -> image.getSubimage(r.x, r.y, r.width, r.height)).toList()));
    }
}