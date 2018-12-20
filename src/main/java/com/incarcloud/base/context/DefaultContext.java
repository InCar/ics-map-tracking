package com.incarcloud.base.context;

import com.incarcloud.base.anno.*;
import com.incarcloud.base.config.Config;
import com.incarcloud.base.config.DataSource;
import com.incarcloud.base.exception.NoHandlerException;
import com.incarcloud.base.request.RequestData;
import com.incarcloud.base.util.ClassUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class DefaultContext implements Context,AutoScanner{
    private Config config;
    private RequestHandler requestHandler;
    private ResourceHandler resourceHandler;
    private Dispatcher dispatcher;
    private boolean isInit=false;

    /**
     * 所有扫描出来的带
     * @see ICSComponent
     * 注解的类
     * 以及
     * 被此注解标注的注解的类
     */
    private final Map<String,Object> beanMap =new HashMap<>();

    private final ConcurrentHashMap<String,Object> typeNameToBeanCacheMap =new ConcurrentHashMap<>();


    public DefaultContext(Config config) {
        this.config = config;
    }

    public DefaultContext init() {
        isInit=true;
        if(config==null){
            throw new RuntimeException("Param[config] Must Not Be Null");
        }
        //1、根据配置初始化所有组件
        scanComponents(config);
        //2、初始化请求分发器
        dispatcher=getBeanByName("dispatcher");
        //3、初始化静态请求处理器
        resourceHandler=getBeanByName("resourceHandler");
        //4、初始化动态请求处理器
        requestHandler=getBeanByName("requestHandler");
        //5、初始化所有组件
        initComponents();
        return this;
    }


    /**
     * 遍历所有bean,如果实现了Initializable接口,则进行初始化
     */
    protected void initComponents(){
        //1、如果实现了Initializable接口,则调用初始化方法
        for (Object e : beanMap.values()) {
            if(e instanceof Initializable){
                ((Initializable)e).init(this);
            }
        }
    }

    /**
     * 扫描所有ICSComponents并注入属性
     * @param config
     */
    protected void scanComponents(Config config){
        //1、找出所有ICSComponent及其子注解 标注的类
        Map<String,List<Class>> map= ClassUtil.findWithSub(ICSComponent.class,config.getScanPackages());
        //2、遍历每一个,生成对象并填入map
        Map<String,List<Class>> conditionalOnMissingBeanClassMap=new HashMap<>();
        map.forEach((k,v)->{
            v.forEach(clazz->{
                //2.1、判断当前类是否有ICSDataSource,如果有则检查值是否和配置一致,不一致则跳过此类
                ICSDataSource icsDataSource=(ICSDataSource) clazz.getAnnotation(ICSDataSource.class);
                if(icsDataSource!=null){
                    DataSource classDataSource= icsDataSource.value();
                    if(config.getDataSource()!=classDataSource){
                        return;
                    }
                }
                //2.2、遇到有ICSConditionalOnMissingBean注解的类,先加入到另一个集合中,最后处理
                ICSConditionalOnMissingBean icsConditionalOnMissingBean=(ICSConditionalOnMissingBean)clazz.getAnnotation(ICSConditionalOnMissingBean.class);
                if(icsConditionalOnMissingBean!=null) {
                    List<Class> classList = conditionalOnMissingBeanClassMap.computeIfAbsent(k, e -> new ArrayList<>());
                    classList.add(clazz);
                    return;
                }
                //2.3、插入bean
                putBeanIntoMap(k,clazz);
            });
        });
        //3、处理ICSConditionalOnMissingBean注解的类
        conditionalOnMissingBeanClassMap.forEach((k,v)-> {
            v.forEach(clazz -> {
                ICSConditionalOnMissingBean icsConditionalOnMissingBean = (ICSConditionalOnMissingBean) clazz.getAnnotation(ICSConditionalOnMissingBean.class);
                //3.1、优先级 name>value
                String name = icsConditionalOnMissingBean.name();
                boolean exist = false;
                if ("".equals(name)) {
                    Class[] classes = icsConditionalOnMissingBean.value();
                    if (classes.length == 0) {
                        Object mapBean = getBeanByType(clazz);
                        if (mapBean != null) {
                            exist = true;
                        }
                    } else {
                        for (Class aClass : classes) {
                            Object mapBean = getBeanByType(aClass);
                            if (mapBean != null) {
                                exist = true;
                                break;
                            }
                        }
                    }
                } else {
                    Object mapBean = getBeanByName(name);
                    if (mapBean != null) {
                        exist = true;
                    }
                }
                //3.2、如果不存在,则加入到map中
                if (!exist) {
                    putBeanIntoMap(k, clazz);
                }
            });
        });
        //4、为map中的对象注入ICSAutowire
        beanMap.values().forEach(e->{
            //4.1、属性注入
            List<Field> fieldList= ClassUtil.getDeclaredFieldListWithAnno(e.getClass(), ICSAutowire.class);
            fieldList.forEach(field->{
                ICSAutowire icsAutowire=field.getAnnotation(ICSAutowire.class);
                String name=icsAutowire.value();
                if("".equals(name)){
                    //4.1.1、通过类型注入
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
                    //4.1.2、通过名称注入
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

        });
        //5、将map变成不可编辑
        Collections.unmodifiableMap(beanMap);
    }

    public <T>T getBeanByType(Class clazz){
        String key=clazz.getName();
        Object obj= typeNameToBeanCacheMap.computeIfAbsent(key, k->{
            List<Object> tempList= beanMap.values().stream().filter(e->clazz.isAssignableFrom(e.getClass())).collect(Collectors.toList());
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

    public <T>T getBeanByName(String name){
        return (T) beanMap.get(name);
    }

    @Override
    public Map<String, Object> getBeanMap() {
        return beanMap;
    }

    private void putBeanIntoMap(String annoClassName,Class clazz){
        String name="";
        if(ICSComponent.class.getName().equals(annoClassName)){
            ICSComponent icsComponent= (ICSComponent)clazz.getAnnotation(ICSComponent.class);
            name=icsComponent.value();

        }else if(ICSController.class.getName().equals(annoClassName)){
            ICSController icsController= (ICSController)clazz.getAnnotation(ICSController.class);
            name=icsController.value();
        }
        if("".equals(name)){
            String simpleName=clazz.getName();
            name=simpleName.substring(0,1).toLowerCase()+simpleName.substring(1);
        }
        if(beanMap.containsKey(name)){
            Object mapObj= beanMap.get(name);
            throw new RuntimeException("ICSContext Init Failed,Component["+mapObj.getClass().getName()+"] Has Same Name as Component["+clazz.getName()+"]");
        }else{
            try {
                Object obj= clazz.newInstance();
                beanMap.put(name,obj);
            } catch (InstantiationException |IllegalAccessException e) {
                throw new RuntimeException("ICSContext Init Failed,Construct Component["+clazz.getName()+"] Failed");
            }
        }
    }



    public DefaultContext withConfig(Config config) {
        if(isInit){
           throw new RuntimeException("Context Has Init,Can't Modify Config");
        }
        this.config=config;
        return this;
    }

    @Override
    public Config getConfig() {
        return this.config;
    }

    @Override
    public void handleRequest(RequestData requestData) {
        requestHandler.handleRequest(requestData);
    }

    @Override
    public void handleResource(RequestData requestData) {
        resourceHandler.handleResource(requestData);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws NoHandlerException{
        RequestData requestData=new RequestData(request,response,config);
        if(requestData.getSubPath()==null){
            throw new NoHandlerException(request.getRequestURI());
        }
        dispatch(requestData);
    }

    @Override
    public void dispatch(RequestData requestData) {
        dispatcher.dispatch(requestData);
    }

    public RequestHandler getRequestHandler() {
        return requestHandler;
    }

    public DefaultContext withRequestHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
        if(requestHandler instanceof Initializable){
            ((Initializable) requestHandler).init(this);
        }
        return this;
    }

    public ResourceHandler getResourceHandler() {
        return resourceHandler;
    }

    public DefaultContext withResourceHandler(ResourceHandler resourceHandler) {
        this.resourceHandler = resourceHandler;
        if(resourceHandler instanceof Initializable){
            ((Initializable) resourceHandler).init(this);
        }
        return this;
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public DefaultContext withDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
        if(dispatcher instanceof Initializable){
            ((Initializable) dispatcher).init(this);
        }
        return this;
    }
}

