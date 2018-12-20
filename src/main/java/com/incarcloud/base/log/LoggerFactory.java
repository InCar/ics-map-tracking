package com.incarcloud.base.log;

import com.incarcloud.base.config.LogConfig;
import com.incarcloud.base.util.FileUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.*;

public class LoggerFactory {
    private static SimpleFormatter getSimpleFormatter(){
        SimpleFormatter simpleFormatter=new SimpleFormatter(){
            private DateTimeFormatter dateTimeFormatter= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.systemDefault());
            @Override
            public synchronized String format(LogRecord record) {
                ////2018-12-12 17:06:17.279 [main] WARN  o.h.e.jdbc.env.internal.JdbcEnvironmentInitiator - HHH000342: Could not obtain connection to query metadata : Communications link failure
                StringBuilder sb=new StringBuilder();
                String dateStr=dateTimeFormatter.format(Instant.ofEpochMilli(record.getMillis()));
                sb.append(dateStr);
                sb.append(" [");
                sb.append(record.getThreadID());
                sb.append("] ");
                sb.append(record.getLevel().getName());
                sb.append("  ");
                sb.append(record.getSourceClassName());
                sb.append(".");
                sb.append(record.getSourceMethodName());
                sb.append(" - ");
                sb.append(formatMessage(record));
                sb.append("\n");
                return sb.toString();
            }
        };
        return simpleFormatter;
    }

    private static ConsoleHandler getConsoleHandler(Formatter formatter) {
        ConsoleHandler consoleHandler=new ConsoleHandler();
        consoleHandler.setFormatter(formatter);
        return consoleHandler;
    }

    private static FileHandler getFileHandler(Formatter formatter,String dir) {
        Path path= Paths.get(dir);
        FileHandler fileHandler= null;
        try {
            FileUtil.createDirectories(path);
            fileHandler = new FileHandler(path+"/ics%u.log",1024000,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileHandler.setFormatter(formatter);
        return fileHandler;
    }

    public synchronized static Logger getLogger(LogConfig logConfig){
        Logger logger=Logger.getLogger("ics");
        logger.setUseParentHandlers(false);
        if(logConfig.isEnableLog()) {
            SimpleFormatter simpleFormatter = getSimpleFormatter();
            if(logConfig.isEnableConsoleLog()){
                logger.addHandler(getConsoleHandler(simpleFormatter));
            }
            if(logConfig.isEnableFileLog()) {
                logger.addHandler(getFileHandler(simpleFormatter, logConfig.getFileLogDir()));
            }
            logger.setLevel(logConfig.getLevel());
        }
        return logger;
    }
}
