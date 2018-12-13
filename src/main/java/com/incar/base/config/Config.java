package com.incar.base.config;


import com.incar.base.log.LoggerFactory;

import java.util.logging.Logger;

/**
 * Dispatcher配置类
 */
public class Config {
    public final static String DEFAULT_MAPPING_PRE="/ics";
    public final static String DEFAULT_ENCODING="UTF-8";


    public static boolean ENABLE_FILTER=false;
    public static Config FILTER_CONFIG;

    //匹配request路径前缀
    private String mappingPre;

    //request和response编码
    private String encoding;

    //是否启用filter拦截
    private boolean enableFilter;

    //logger
    private LogConfig logConfig;
    private Logger logger;

    public Config() {
        this.mappingPre = DEFAULT_MAPPING_PRE;
        this.encoding = DEFAULT_ENCODING;
        this.enableFilter = ENABLE_FILTER;
        this.logger= LoggerFactory.getLogger(new LogConfig());
    }

    public String getMappingPre() {
        return mappingPre;
    }

    public Config withMappingPre(String mappingPre) {
        this.mappingPre = mappingPre;
        return this;
    }

    public String getEncoding() {
        return encoding;
    }

    public Config withEncoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    public boolean isEnableFilter() {
        return enableFilter;
    }

    public Config withEnableFilter(boolean enableFilter) {
        this.enableFilter = enableFilter;
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

    /**
     * 开启过滤器,全局唯一
     * @see javax.servlet.annotation.WebFilter 过滤器
     * @see com.incar.business.filter.ICSFilter
     * @param config
     */
    public static void enableFilter(Config config){
        Config.ENABLE_FILTER=true;
        Config.FILTER_CONFIG=config;

    }
}
