package com.github.johypark97.varchivemacro.macro.config;

import com.github.johypark97.varchivemacro.lib.common.json.CustomGsonBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class ConfigManager implements IConfigObservable {
    // singleton
    private ConfigManager() {
    }

    private static final class ConfigManagerInstance {
        private static final ConfigManager instance = new ConfigManager();
    }

    public static ConfigManager getInstance() {
        return ConfigManagerInstance.instance;
    }

    // observer
    private final Set<IConfigObserver> observers = new HashSet<>();

    @Override
    public synchronized void register(IConfigObserver observer) {
        if (observer == null) {
            throw new NullPointerException();
        }
        observers.add(observer);
    }

    @Override
    public synchronized void unregister(IConfigObserver observer) {
        if (observer == null) {
            throw new NullPointerException();
        }
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(NotifyType type) {
        IConfigObserver[] localObservers;
        synchronized (this) {
            localObservers = observers.toArray(IConfigObserver[]::new);
        }

        for (IConfigObserver observer : localObservers) {
            switch (type) {
                case IS_LOADED -> observer.configIsLoaded(getData());
                case WILL_BE_SAVED -> observer.configWillBeSaved(getData());
                default -> throw new RuntimeException("unknown notify type");
            }
        }
    }

    // const
    private static final Path CONFIG_PATH = Path.of(System.getProperty("user.dir"), "config.json");

    // instance
    private final Gson gson = CustomGsonBuilder.create();
    private volatile ConfigData data;

    public ConfigData getData() {
        if (data == null) {
            synchronized (this) {
                if (data == null) {
                    data = new ConfigData();
                }
            }
        }

        return data;
    }

    public String convertDataToJson() {
        return gson.toJson(getData());
    }

    public ConfigData convertJsonToData(String json) throws JsonSyntaxException {
        return gson.fromJson(json, ConfigData.class);
    }

    public boolean isConfigExists() {
        return Files.exists(CONFIG_PATH);
    }

    public synchronized void save() throws IOException {
        notifyObservers(NotifyType.WILL_BE_SAVED);
        Files.writeString(CONFIG_PATH, convertDataToJson());
    }

    public synchronized void load() throws IOException, JsonSyntaxException {
        ConfigData loaded = convertJsonToData(Files.readString(CONFIG_PATH));
        if (loaded != null) {
            data = loaded;
        }

        notifyObservers(NotifyType.IS_LOADED);
    }
}
