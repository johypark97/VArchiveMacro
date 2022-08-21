package com.github.johypark97.varchivemacro.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import com.github.johypark97.varchivemacro.Main;

public class Version {
    public static final String version;

    static {
        String value = null;

        URL url = Main.class.getClassLoader().getResource("META-INF/MANIFEST.MF");
        if (url != null) {
            try (InputStream stream = url.openStream()) {
                Attributes attributes = new Manifest(stream).getMainAttributes();
                value = attributes.getValue("Implementation-Version");
            } catch (IOException e) {
            }
        }

        version = (value != null) ? value : "";
    }
}
