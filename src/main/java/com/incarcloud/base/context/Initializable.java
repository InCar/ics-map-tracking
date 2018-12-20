package com.incarcloud.base.context;

/**
 * 可初始化接口,如果ICSComponent实现了此接口,会在Context初始化时候调用此方法
 */
public interface Initializable {
    void init(Context context);
}
