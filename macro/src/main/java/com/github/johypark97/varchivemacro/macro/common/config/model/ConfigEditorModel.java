package com.github.johypark97.varchivemacro.macro.common.config.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConfigEditorModel {
    public interface Config<C extends Record & Config<C, E>, E extends Editor<C, E>> {
        E edit();
    }

    public abstract static class Editor<C extends Record & Config<C, E>, E extends Editor<C, E>> {
        public abstract C commit();

        protected void copyFrom(C config) {
            Objects.requireNonNull(config);

            Map<String, Field> editorFieldNameMap = new HashMap<>();
            for (Field editorField : this.getClass().getDeclaredFields()) {
                ConfigField annotation = editorField.getAnnotation(ConfigField.class);
                if (annotation == null) {
                    continue;
                }

                String editorFieldName =
                        annotation.value().isEmpty() ? editorField.getName() : annotation.value();
                editorFieldNameMap.put(editorFieldName, editorField);
            }

            for (RecordComponent configComponent : config.getClass().getRecordComponents()) {
                String configFieldName = configComponent.getName();

                Field editorField = editorFieldNameMap.remove(configFieldName);
                if (editorField == null) {
                    throw new IllegalStateException(
                            "No matching editor field for config field: " + configFieldName);
                }

                try {
                    Object configValue = configComponent.getAccessor().invoke(config);
                    editorField.setAccessible(true); // NOPMD
                    editorField.set(this, configValue);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }

            if (!editorFieldNameMap.isEmpty()) {
                throw new IllegalStateException(
                        "Unmatched editor fields: " + editorFieldNameMap.keySet());
            }
        }

        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        protected @interface ConfigField {
            String value() default "";
        }
    }
}
