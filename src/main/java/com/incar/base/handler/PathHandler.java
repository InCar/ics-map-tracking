package com.incar.base.handler;


import com.incar.base.config.Config;

public abstract class PathHandler implements OrderedHandler {
    protected String path;
    protected Config config;
    public PathHandler(Config config,String path){
        this.config=config;
        this.path=path;
    }

    public PathHandler withPath(String path) {
        this.path = path;
        return this;
    }

    public String getPath() {
        return path;
    }
}
