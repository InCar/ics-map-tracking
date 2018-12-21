package com.incarcloud.base.config;


import com.incarcloud.base.log.LoggerFactory;

import java.util.logging.Logger;

/**
 * Dispatcher配置类
 */
public class Config {
    public final static String DEFAULT_REQUEST_MAPPING_PRE ="/ics";
    public final static String DEFAULT_REQUEST_STATIC_MAPPING_PRE="/static/";
    public final static String DEFAULT_FILE_STATIC_MAPPING_PRE ="/ics/static/";
    public final static String DEFAULT_ENCODING="UTF-8";
    public final static String[] DEFAULT_SCAN_PACKAGES=new String[]{"com.incarcloud"};

    //匹配request路径前缀
    private String requestMappingPre;

    //静态资源请求匹配前缀
    private String requestStaticMappingPre;
    //静态资源文件路径匹配前缀
    private String fileStaticMappingPre;

    //response编码
    private String encoding;

    //logger
    private LogConfig logConfig;
    private Logger logger;

    //mysql连接配置
    private DataSource dataSource;
    private MysqlConfig mysqlConfig;
    private HBaseConfig hBaseConfig;

    //扫面ics组件的包路径
    private String[] scanPackages;


    public Config() {
        this.requestMappingPre = DEFAULT_REQUEST_MAPPING_PRE;
        this.encoding = DEFAULT_ENCODING;
        this.requestStaticMappingPre = DEFAULT_REQUEST_STATIC_MAPPING_PRE;
        this.fileStaticMappingPre=DEFAULT_FILE_STATIC_MAPPING_PRE;
        this.logConfig=new LogConfig();
        this.logger= LoggerFactory.getLogger(logConfig);
        this.dataSource=DataSource.Other;
        this.scanPackages=DEFAULT_SCAN_PACKAGES;
    }

    public String getRequestMappingPre() {
        return requestMappingPre;
    }

    public Config withRequestMappingPre(String requestMappingPre) {
        this.requestMappingPre = requestMappingPre;
        return this;
    }

    public String getEncoding() {
        return encoding;
    }

    public Config withEncoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    public Logger getLogger() {
        return logger;
    }

    public Config withLogger(Logger logger) {
        this.logger = logger;
        return this;
    }

    public LogConfig getLogConfig() {
        return logConfig;
    }

    public Config withLogConfig(LogConfig logConfig) {
        this.logConfig = logConfig;
        this.logger=LoggerFactory.getLogger(logConfig);
        return this;
    }

    public String getRequestStaticMappingPre() {
        return requestStaticMappingPre;
    }

    public Config withRequestStaticMappingPre(String requestStaticMappingPre) {
        this.requestStaticMappingPre = requestStaticMappingPre;
        return this;
    }

    public String getFileStaticMappingPre() {
        return fileStaticMappingPre;
    }

    public Config withFileStaticMappingPre(String fileStaticMappingPre) {
        this.fileStaticMappingPre = fileStaticMappingPre;
        return this;
    }

    public MysqlConfig getMysqlConfig() {
        return mysqlConfig;
    }

    public Config withMysqlConfig(MysqlConfig mysqlConfig) {
        this.mysqlConfig = mysqlConfig;
        dataSource=DataSource.MYSQL;
        return this;
    }

    public HBaseConfig gethBaseConfig() {
        return hBaseConfig;
    }

    public Config withhBaseConfig(HBaseConfig hBaseConfig) {
        this.hBaseConfig = hBaseConfig;
        dataSource=DataSource.HBase;
        return this;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public Config withDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public String[] getScanPackages() {
        return scanPackages;
    }

    public Config withScanPackages(String... scanPackages) {
        this.scanPackages = scanPackages;
        return this;
    }
}
