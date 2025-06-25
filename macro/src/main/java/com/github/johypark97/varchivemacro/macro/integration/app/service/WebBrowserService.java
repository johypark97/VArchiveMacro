package com.github.johypark97.varchivemacro.macro.integration.app.service;

import javafx.application.HostServices;

public class WebBrowserService {
    private final HostServices hostServices;

    public WebBrowserService(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    public void open(String url) {
        hostServices.showDocument(url);
    }
}
