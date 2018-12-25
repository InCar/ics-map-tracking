package com.incarcloud.base.handler.dynamicrequest.request.impl;

import com.incarcloud.base.anno.ICSRequestMapping;
import com.incarcloud.base.anno.ICSRequestParam;
import com.incarcloud.base.handler.dynamicrequest.convert.impl.ArrayParamConverter;
import com.incarcloud.base.handler.dynamicrequest.convert.impl.DateParamConverter;
import com.incarcloud.base.handler.dynamicrequest.convert.impl.NumberParamConverter;
import com.incarcloud.base.handler.dynamicrequest.convert.impl.StringParamConverter;
import com.incarcloud.base.handler.dynamicrequest.data.ICSHttpRequestParam;
import com.incarcloud.base.handler.dynamicrequest.define.ICSHttpRequestMethodEnum;
import com.incarcloud.base.handler.dynamicrequest.request.DynamicRequestHandler;
import com.incarcloud.base.request.RequestData;
import com.incarcloud.base.util.ClassUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class SimpleRequestHandler implements DynamicRequestHandler {
    private String path;
    private ICSHttpRequestMethodEnum icsHttpRequestMethodEnum;
    private LinkedHashMap<String, ICSHttpRequestParam> paramMap;
    private Object controllerObj;
    private Method method;
    private Class returnClass;

    public Class getReturnClass() {
        return returnClass;
    }

    public void setReturnClass(Class returnClass) {
        this.returnClass = returnClass;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ICSHttpRequestMethodEnum getIcsHttpRequestMethodEnum() {
        return icsHttpRequestMethodEnum;
    }

    public void setIcsHttpRequestMethodEnum(ICSHttpRequestMethodEnum icsHttpRequestMethodEnum) {
        this.icsHttpRequestMethodEnum = icsHttpRequestMethodEnum;
    }

    public LinkedHashMap<String, ICSHttpRequestParam> getParamMap() {
        return paramMap;
    }

    public void setParamMap(LinkedHashMap<String, ICSHttpRequestParam> paramMap) {
        this.paramMap = paramMap;
    }

    public Object getControllerObj() {
        return controllerObj;
    }

    public void setControllerObj(Object controllerObj) {
        this.controllerObj = controllerObj;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }


    public SimpleRequestHandler(String path, ICSHttpRequestMethodEnum icsHttpRequestMethodEnum, LinkedHashMap<String, ICSHttpRequestParam> paramMap, Object controllerObj, Class returnClass, Method method) {
        this.path = path;
        this.icsHttpRequestMethodEnum = icsHttpRequestMethodEnum;
        this.paramMap = paramMap;
        this.controllerObj=controllerObj;
        this.returnClass=returnClass;
        this.method=method;
    }

    /**
     * 执行方法
     * @param requestData
     * @return
     */
    public Object handle(RequestData requestData) throws Exception{
        String method=requestData.getRequest().getMethod();
        //如果请求方式不匹配,返回异常
        if(!icsHttpRequestMethodEnum.name().equals(method)){
            throw new RuntimeException("Method["+method+"] Not Support");
        }
        //验证参数必填
        HttpServletRequest request= requestData.getRequest();
        HttpServletResponse response= requestData.getResponse();
        Set<String> paramSet=request.getParameterMap().keySet();
        //过滤出不属于request、response且参数必填并且未传递进入
        List<ICSHttpRequestParam> missParamList=this.paramMap.values().stream().filter(e->!e.getClazz().isAssignableFrom(HttpServletRequest.class)&&!e.getClazz().isAssignableFrom(HttpServletResponse.class)&&e.getRequired()&&!paramSet.contains(e.getName())).collect(Collectors.toList());
        if(missParamList.size()>0){
            String paramStr=missParamList.stream().map(e->e.getName()).reduce((e1,e2)->e1+","+e2).get();
            throw new RuntimeException("Param["+paramStr+"] Required");
        }
        //拼装参数,按照定义的参数顺序设值
        List<Object> paramList=new ArrayList<>();
        Map<String,String[]> passParamMap= request.getParameterMap();
        this.paramMap.forEach((k,v)->{
            if(v.getClazz().isAssignableFrom(HttpServletRequest.class)){
                paramList.add(request);
            }else if (v.getClazz().isAssignableFrom(HttpServletResponse.class)){
                paramList.add(response);
            }else {
                String[] val = passParamMap.get(k);
                if (val == null) {
                    String defaultValue=v.getDefaultValue();
                    if("".equals(defaultValue)){
                        paramList.add(null);
                    }else{
                        paramList.add(convertParam(v,new String[]{v.getDefaultValue()}));
                    }
                } else {
                    paramList.add(convertParam(v, val));
                }
            }
        });
        //调用方法
        return this.method.invoke(controllerObj,paramList.toArray());
    }

    private Object convertParam(ICSHttpRequestParam param,String[] values){
        Class targetType=param.getClazz();
        if(String.class.isAssignableFrom(targetType)){
            return StringParamConverter.INSTANCE.convert(values,targetType);
        }else if(Number.class.isAssignableFrom(targetType)){
            return NumberParamConverter.INSTANCE.convert(values,targetType);
        }else if(Date.class.isAssignableFrom(targetType)){
            return DateParamConverter.INSTANCE.convert(values,targetType);
        }else if(targetType.isArray()){
            return ArrayParamConverter.INSTANCE.convert(values,targetType);
        }else{
            String arrStr=Arrays.stream(values).reduce((e1,e2)->e1+","+e2).orElse("");
            throw new RuntimeException("Param["+param.getName()+"] Type["+param.getClazz().getName()+"] Value["+arrStr+"] Converter Not Support");
        }
    }

    /**
     * 生成一个ICSController的 ICSHttpRequestMethod 集合
     * @param controllerObj
     * @return
     */
    public static List<SimpleRequestHandler> generateByICSController(Object controllerObj){
        Class clazz=controllerObj.getClass();
        String[] pre={""};
        ICSRequestMapping controllerRequestMapping= (ICSRequestMapping)clazz.getAnnotation(ICSRequestMapping.class);
        if(controllerRequestMapping!=null){
            String value=controllerRequestMapping.value();
            pre[0]=value;
        }
        List<Method> methodList= ClassUtil.getDeclaredMethodListWithAnno(clazz, ICSRequestMapping.class);
        return methodList.stream().map(method->{
            ICSRequestMapping methodRequestMapping= method.getAnnotation(ICSRequestMapping.class);
            String value=methodRequestMapping.value();
            String path=pre[0]+value;
            ICSHttpRequestMethodEnum httpRequestMethodEnum =methodRequestMapping.method();
            LinkedHashMap<String, ICSHttpRequestParam> paramMap=new LinkedHashMap<>();
            for (Parameter parameter : method.getParameters()) {
                ICSRequestParam requestParam= parameter.getAnnotation(ICSRequestParam.class);
                String name=requestParam.value();
                paramMap.put(name,new ICSHttpRequestParam(name,parameter.getType(),requestParam.required(),requestParam.defaultValue()));
            }
            return new SimpleRequestHandler(path, httpRequestMethodEnum,paramMap,controllerObj,method.getReturnType(),method);
        }).collect(Collectors.toList());
    }
}
