package com.incar.base.config;

import java.util.logging.Level;

public class LogConfig {
    //是否启用日志
    private boolean enableLog;

    //日志等级
    private Level level;

    //控制台日志
    //是否启用控制台日志
    private boolean enableConsoleLog;

    //文件日志
    //是否启用文件日志
    private boolean enableFileLog;
    private String fileLogDir;

    public LogConfig() {
        enableLog=true;
        level=Level.ALL;
        enableConsoleLog=true;
        enableFileLog=true;
        fileLogDir="log";
    }

    public boolean isEnableLog() {
        return enableLog;
    }

    public LogConfig withEnableLog(boolean enableLog) {
        this.enableLog = enableLog;
        return this;
    }

    public Level getLevel() {
        return level;
    }

    public LogConfig withLevel(Level level) {
        this.level = level;
        return this;
    }

    public boolean isEnableConsoleLog() {
        return enableConsoleLog;
    }

    public LogConfig withEnableConsoleLog(boolean enableConsoleLog) {
        this.enableConsoleLog = enableConsoleLog;
        return this;
    }

    public boolean isEnableFileLog() {
        return enableFileLog;
    }

    public LogConfig withEnableFileLog(boolean enableFileLog) {
        this.enableFileLog = enableFileLog;
        return this;
    }

    public String getFileLogDir() {
        return fileLogDir;
    }

    public LogConfig withFileLogDir(String fileLogDir) {
        this.fileLogDir = fileLogDir;
        return this;
    }
}
