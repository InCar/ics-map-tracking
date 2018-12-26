package com.incarcloud.base.config;

public class JdbcConfig {
    private String driverClassName;
    private String url;
    private String user;
    private String password;
    private Integer poolSize;
    private Integer maxWaitSeconds;

    public JdbcConfig(String driverClassName,String url, String user, String password) {
        this.driverClassName=driverClassName;
        this.url = url;
        this.user = user;
        this.password = password;
        this.poolSize=10;
        this.maxWaitSeconds=30;
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public JdbcConfig withDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public JdbcConfig withUrl(String url) {
        this.url = url;
        return this;
    }

    public String getUser() {
        return user;
    }

    public JdbcConfig withUser(String user) {
        this.user = user;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public JdbcConfig withPassword(String password) {
        this.password = password;
        return this;
    }

    public Integer getPoolSize() {
        return poolSize;
    }

    public JdbcConfig withPoolSize(Integer poolSize) {
        this.poolSize = poolSize;
        return this;
    }

    public Integer getMaxWaitSeconds() {
        return maxWaitSeconds;
    }

    public JdbcConfig withMaxWaitSeconds(Integer maxWaitSeconds) {
        this.maxWaitSeconds = maxWaitSeconds;
        return this;
    }
}
