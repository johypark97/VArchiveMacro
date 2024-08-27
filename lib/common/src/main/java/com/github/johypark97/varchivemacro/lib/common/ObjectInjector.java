package com.github.johypark97.varchivemacro.lib.common;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;

public class ObjectInjector {
    private static final Package ANNOTATION_PACKAGE = Annotation.class.getPackage();

    public static Object get(Object instance, String key) throws ReflectiveOperationException {
        Objects.requireNonNull(instance);
        Objects.requireNonNull(key);

        return getAnnotatedField(instance, key).get(instance);
    }

    public static void set(Object instance, String key, Object value)
            throws ReflectiveOperationException {
        Objects.requireNonNull(instance);
        Objects.requireNonNull(key);

        getAnnotatedField(instance, key).set(instance, value);
    }

    public static <T extends Annotation> Map<Field, List<T>> findAnnotatedField(Object instance,
            Class<T> annotationClass) {
        Objects.requireNonNull(annotationClass);
        Objects.requireNonNull(instance);

        Map<Field, List<T>> map = new HashMap<>();

        for (Field field : instance.getClass().getFields()) {
            Stack<Annotation> stack = new Stack<>();
            reversedAddAll(stack, field.getAnnotations());

            Set<Annotation> set = new HashSet<>();
            while (!stack.isEmpty()) {
                Annotation annotation = stack.pop();

                if (annotation.annotationType().getPackage().equals(ANNOTATION_PACKAGE)
                        || set.contains(annotation)) {
                    continue;
                }
                set.add(annotation);

                if (annotationClass.isInstance(annotation)) {
                    map.computeIfAbsent(field, x -> new ArrayList<>())
                            .add(annotationClass.cast(annotation));
                }

                reversedAddAll(stack, annotation.annotationType().getAnnotations());
            }
        }

        return map;
    }

    protected static List<Field> filterAnnotatedField(Object instance, String key)
            throws NoSuchFieldException {
        Map<Field, List<InjectedObject>> map = findAnnotatedField(instance, InjectedObject.class);
        if (map.isEmpty()) {
            throw new NoSuchFieldException(
                    String.format("No %s annotated fields in %s.", InjectedObject.class.getName(),
                            instance.getClass()));
        }

        return map.entrySet().stream().filter(x -> x.getValue().get(0).key().equals(key))
                .map(Entry::getKey).toList();
    }

    protected static Field getAnnotatedField(Object instance, String key)
            throws NoSuchFieldException {
        List<Field> list = filterAnnotatedField(instance, key);

        if (list.isEmpty()) {
            throw new NoSuchFieldException(
                    String.format("No %s annotated fields with key %s in %s.",
                            InjectedObject.class.getName(), key, instance.getClass()));
        } else if (list.size() > 1) {
            throw new IllegalStateException(
                    String.format("Too many %s annotated fields with key %s in %s.",
                            InjectedObject.class.getName(), key, instance.getClass()));
        }

        return list.get(0);
    }

    @SafeVarargs
    private static <T> void reversedAddAll(Collection<? super T> c, T... e) {
        for (int i = e.length - 1; i >= 0; i--) {
            c.add(e[i]);
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD})
    public @interface InjectedObject {
        String key();
    }
}
