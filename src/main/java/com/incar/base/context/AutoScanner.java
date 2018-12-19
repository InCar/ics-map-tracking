package com.incar.base.context;

import java.util.Map;

public interface AutoScanner {
     <T>T getBeanByType(Class clazz);
     <T>T getBeanByName(String name);
     Map<String,Object> getBeanMap();
}
