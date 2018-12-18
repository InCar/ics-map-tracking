package com.incar.base.handler.dynamicrequest.component;

import com.incar.base.config.Config;

public class BaseComponent {
    protected Config config;

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }
}
