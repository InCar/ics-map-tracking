package com.incarcloud.base.config;

public class MysqlConfig {
    private String url;
    private String user;
    private String password;

    public MysqlConfig(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }


    public String getUrl() {
        return url;
    }

    public MysqlConfig withUrl(String url) {
        this.url = url;
        return this;
    }

    public String getUser() {
        return user;
    }

    public MysqlConfig withUser(String user) {
        this.user = user;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public MysqlConfig withPassword(String password) {
        this.password = password;
        return this;
    }
}
