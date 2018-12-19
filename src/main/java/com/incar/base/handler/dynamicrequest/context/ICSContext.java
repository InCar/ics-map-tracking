package com.incar.base.handler.dynamicrequest.context;

import com.incar.base.config.Config;
import com.incar.base.config.DataSource;
import com.incar.base.handler.dynamicrequest.anno.*;
import com.incar.base.handler.dynamicrequest.component.BaseComponent;
import com.incar.base.util.ClassUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ICSContext {
    /**
     * 所有扫描出来的带
     * @see com.incar.base.handler.dynamicrequest.anno.ICSComponent
     * 注解的类
     * 以及
     * 被此注解标注的注解的类
     */
    private final static Map<String,Object> NAME_TO_COMPONENT_MAP=new HashMap<>();

    private final static ConcurrentHashMap<String,Object> TYPE_NAME_TO_COMPONENT_CACHE_MAP=new ConcurrentHashMap<>();

    public static <T>T getBeanByType(Class clazz){
        String key=clazz.getName();
        Object obj=TYPE_NAME_TO_COMPONENT_CACHE_MAP.computeIfAbsent(key,k->{
            List<Object> tempList=NAME_TO_COMPONENT_MAP.values().stream().filter(e->clazz.isAssignableFrom(e.getClass())).collect(Collectors.toList());
            int objCount=tempList.size();
            switch (objCount){
                case 0:{
                    return null;
                }
                case 1:{
                    return tempList.get(0);
                }
                default:{
                    List<Object> primaryObjList=tempList.stream().filter(e->e.getClass().getAnnotation(ICSPrimary.class)!=null).collect(Collectors.toList());
                    int primaryObjCount=primaryObjList.size();
                    switch (primaryObjCount){
                        case 0:{
                            throw new RuntimeException("Type ["+clazz.getName()+"] Has More Than One Instance And No ICSPrimary");
                        }
                        case 1:{
                            return primaryObjList.get(0);
                        }
                        default:{
                            throw new RuntimeException("Type ["+clazz.getName()+"] Has More Than One ICSPrimary Instance");
                        }
                    }
                }
            }
        });
        return (T)obj;
    }

    public static <T>T getBeanByName(String name){
        return (T)NAME_TO_COMPONENT_MAP.get(name);
    }

    public static Map<String,Object> getBeanMap(){
        return new HashMap<>(NAME_TO_COMPONENT_MAP);
    }

    public static void init(Config config,String ... scanPackages){
        //1、找出所有ICSComponent及其子注解 标注的类
        Map<String,List<Class>> map=ClassUtil.findWithSub(ICSComponent.class,scanPackages);
        //2、遍历每一个,生成对象并填入map
        map.forEach((k,v)->{
            v.forEach(clazz->{
                //判断当前类是否有ICSDataSource,如果有则检查值是否和配置一致,不一致则跳过此类
                ICSDataSource icsDataSource=(ICSDataSource) clazz.getAnnotation(ICSDataSource.class);
                if(icsDataSource!=null){
                    DataSource classDataSource= icsDataSource.value();
                    if(config.getDataSource()!=classDataSource){
                        return;
                    }
                }
                String name="";
                if(ICSComponent.class.getName().equals(k)){
                    ICSComponent icsComponent= (ICSComponent)clazz.getAnnotation(ICSComponent.class);
                    name=icsComponent.value();

                }else if(ICSController.class.getName().equals(k)){
                    ICSController icsController= (ICSController)clazz.getAnnotation(ICSController.class);
                    name=icsController.value();
                }
                if("".equals(name)){
                    String simpleName=clazz.getName();
                    name=simpleName.substring(0,1).toLowerCase()+simpleName.substring(1);
                }
                if(NAME_TO_COMPONENT_MAP.containsKey(name)){
                    Object mapObj= NAME_TO_COMPONENT_MAP.get(name);
                    throw new RuntimeException("ICSContext Init Failed,Component["+mapObj.getClass().getName()+"] Has Same Name as Component["+clazz.getName()+"]");
                }else{
                    try {
                        Object obj= clazz.newInstance();
                        NAME_TO_COMPONENT_MAP.put(name,obj);
                    } catch (InstantiationException |IllegalAccessException e) {
                        throw new RuntimeException("ICSContext Init Failed,Construct Component["+clazz.getName()+"] Failed");
                    }
                }
            });
        });
        //3、为map中的对象注入ICSAutowire
        NAME_TO_COMPONENT_MAP.values().forEach(e->{
            List<Field> fieldList= ClassUtil.getFieldListWithAnno(e.getClass(), ICSAutowire.class);
            fieldList.forEach(field->{
                ICSAutowire icsAutowire=field.getAnnotation(ICSAutowire.class);
                String name=icsAutowire.value();
                if("".equals(name)){
                    //通过类型注入
                    Class fieldType=field.getType();
                    Object val= getBeanByType(fieldType);
                    if(val==null){
                        throw new RuntimeException("ICSContext Init Failed,Object["+e.toString()+"] Field["+field.getName()+"] Don't Has Component Type["+fieldType.getName()+"]");
                    }else{
                        field.setAccessible(true);
                        try {
                            field.set(e,val);
                        } catch (IllegalAccessException e1) {
                            throw new RuntimeException("ICSContext Init Failed,Object["+e.toString()+"] Field["+field.getName()+"] Can't Set");
                        }
                    }
                }else{
                    //通过名称注入
                    Object val= getBeanByName(name);
                    if(val==null){
                        throw new RuntimeException("ICSContext Init Failed,Object["+e.toString()+"] Field["+field.getName()+"] Don't Has Component Name["+name+"]");
                    }else{
                        field.setAccessible(true);
                        try {
                            field.set(e,val);
                        } catch (IllegalAccessException e1) {
                            throw new RuntimeException("ICSContext Init Failed,Object["+e.toString()+"] Field["+field.getName()+"] Can't Set");
                        }
                    }
                }
            });
            //如果是继承BaseComponent,设置config属性
            if(e instanceof BaseComponent){
                ((BaseComponent)e).setConfig(config);
            }
        });
        //4、将map变成不可编辑
        Collections.unmodifiableMap(NAME_TO_COMPONENT_MAP);
    }
}
