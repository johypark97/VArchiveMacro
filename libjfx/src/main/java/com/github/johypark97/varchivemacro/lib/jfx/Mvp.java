package com.github.johypark97.varchivemacro.lib.jfx;

import com.github.johypark97.varchivemacro.lib.common.ObjectInjector;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

public class Mvp extends ObjectInjector {
    private static final String INJECTED_OBJECT_KEY_MVP_PRESENTER = "INJECTED_MVP_PRESENTER";
    private static final String INJECTED_OBJECT_KEY_MVP_VIEW = "INJECTED_MVP_VIEW";

    public static void hookWindowCloseRequest(Window window,
            EventHandler<WindowEvent> eventHandler) {
        window.setOnCloseRequest(event -> {
            event.consume();
            eventHandler.handle(event);
        });
    }

    public static void loadFxml(Parent root, URL url, Consumer<FXMLLoader> consumer)
            throws IOException {
        Objects.requireNonNull(root);
        Objects.requireNonNull(url);

        FXMLLoader fxmlLoader = new FXMLLoader(url);
        fxmlLoader.setController(root);
        fxmlLoader.setRoot(root);

        if (consumer != null) {
            consumer.accept(fxmlLoader);
        }

        fxmlLoader.load();
    }

    public static <V extends MvpView<V, P>, P extends MvpPresenter<V, P>> void linkViewAndPresenter(
            V view, P presenter) {
        Objects.requireNonNull(presenter);
        Objects.requireNonNull(view);

        try {
            linkIfAbsent(presenter, INJECTED_OBJECT_KEY_MVP_VIEW, view);
            linkIfAbsent(view, INJECTED_OBJECT_KEY_MVP_PRESENTER, presenter);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    protected static void linkIfAbsent(MvpObject instance, String key, MvpObject value)
            throws ReflectiveOperationException {
        Field field = getAnnotatedField(instance, key);

        Object o = field.get(instance);
        if (o != null) {
            throw new IllegalStateException(
                    String.format("%s is already linked to %s.", instance, o));
        }

        field.set(instance, value);
    }

    public interface MvpPresenter<V extends MvpView<V, P>, P extends MvpPresenter<V, P>>
            extends MvpObject {
        @InjectedObject(key = INJECTED_OBJECT_KEY_MVP_VIEW)
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        @interface MvpView {
        }
    }


    public interface MvpView<V extends MvpView<V, P>, P extends MvpPresenter<V, P>>
            extends MvpObject {
        @InjectedObject(key = INJECTED_OBJECT_KEY_MVP_PRESENTER)
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        @interface MvpPresenter {
        }
    }


    protected interface MvpObject {
    }
}
