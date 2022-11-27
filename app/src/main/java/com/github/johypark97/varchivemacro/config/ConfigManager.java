package com.github.johypark97.varchivemacro.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import com.github.johypark97.varchivemacro.util.CustomGsonBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class ConfigManager implements IConfigObservable {
    // singleton
    private ConfigManager() {
        init();
    }

    private static final class ConfigManagerInstance {
        private static final ConfigManager instance = new ConfigManager();
    }

    public static ConfigManager getInstance() {
        return ConfigManagerInstance.instance;
    }

    // observer
    private Set<IConfigObserver> observers = new HashSet<>();

    @Override
    public synchronized void register(IConfigObserver observer) {
        if (observer == null)
            throw new NullPointerException();
        observers.add(observer);
    }

    @Override
    public synchronized void unregister(IConfigObserver observer) {
        if (observer == null)
            throw new NullPointerException();
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

    // instance
    private static final String RESOURCE_BASE_NAME = "config";
    private static final String RESOURCE_FILENAME = "config.filename";
    private static final String RESOURCE_PATH = "config.path";

    private ConfigData data;
    private Gson gson = CustomGsonBuilder.create();
    private Path configPath;

    private void init() {
        ResourceBundle bundle = ResourceBundle.getBundle(RESOURCE_BASE_NAME);
        String dirPath = System.getProperty(bundle.getString(RESOURCE_PATH));
        String filename = bundle.getString(RESOURCE_FILENAME);
        configPath = Path.of(dirPath, filename);
    }

    public ConfigData getData() {
        if (data == null) {
            synchronized (this) {
                if (data == null)
                    data = new ConfigData();
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
        return Files.exists(configPath);
    }

    public synchronized void save() throws IOException {
        notifyObservers(NotifyType.WILL_BE_SAVED);
        Files.writeString(configPath, convertDataToJson());
    }

    public synchronized void load() throws IOException, JsonSyntaxException {
        ConfigData loaded = convertJsonToData(Files.readString(configPath));
        if (loaded != null)
            data = loaded;

        notifyObservers(NotifyType.IS_LOADED);
    }
}
