package com.nlife.wechat.msg;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReflectUtil;
import com.nlife.wechat.msg.annotation.WechatController;
import com.nlife.wechat.msg.annotation.WechatMapping;
import com.nlife.wechat.msg.constants.MsgType;
import com.nlife.wechat.msg.constants.WechatReturn;
import com.nlife.wechat.msg.util.TextMsgWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

/**
 * @Author nlife
 * @Date 2020/5/15
 * @Email xiamisspan@163.com
 */
public class WechatExecutor implements SmartInitializingSingleton, DisposableBean, ApplicationContextAware {

    private  Logger logger = LoggerFactory.getLogger(TextMsgWriter.class);

    /**
     * 需要扫描的包
     */
    private String[] scanPackages;

    private ApplicationContext context;

    private static ConcurrentMap<String, List<Class>> classConcurrentMap = new ConcurrentHashMap<>();

    private static ConcurrentMap<Class, ConcurrentMap<String,ConcurrentMap<Integer,Method>>> methodConcurrentMap = new ConcurrentHashMap<>();

    public void setScanPackages(String[] scanPackages) {this.scanPackages = scanPackages;}

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    /**
     * 具体的处理逻辑
     * @param requestParam
     * @return
     */
    public Object handleRequest(Map<String,String> requestParam){
        //找到对应处理的类-方法
        String msgType = requestParam.get("MsgType");
        List<Class> classList = classConcurrentMap.get(msgType);
        if (classList == null || classList.size() == 0){
            return WechatReturn.SUCCESS;
        }
        if (MsgType.TEXT.equals(msgType)){
            return handleText(classList,requestParam);
        }else if (MsgType.EVENT.equals(msgType)){
            return handleEvent(classList,requestParam);
        }
        return WechatReturn.SUCCESS;
    }


    /**
     * 当所有单例bean初始化完成以后会回调该方法
     */
    @Override
    public void afterSingletonsInstantiated() {
        scanPackages();
    }

    /**
     * 销毁时调用此方法
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        this.classConcurrentMap = null;
        this.methodConcurrentMap = null;
    }

    /**
     * 扫描对应包下的类
     */
    private void scanPackages(){
        Assert.noNullElements(scanPackages,"scanPackages must not null");
        Arrays.stream(scanPackages).forEach(i ->{
            Set<Class<?>> classSet = ClassUtil.scanPackage(i);
            classSet.stream().forEach( j ->{
                if (j.isAnnotationPresent(WechatController.class)){
                    WechatController wechatController = j.getAnnotation(WechatController.class);
                    logger.debug("==> scan handle class :{} ,handle type:{}",j.toString(),wechatController.value());
                    List<Class> classes = classConcurrentMap.get(wechatController.value());
                    if (classes == null){
                        classes = new ArrayList<>();
                    }
                    classes.add(j);
                    classConcurrentMap.put(wechatController.value(),classes);

                    //处理此类中的方法
                    for (Method method: j.getMethods()){
                        WechatMapping wechatMapping = method.getAnnotation(WechatMapping.class);
                        if (wechatMapping != null){
                            logger.debug("====> scan handle class:{} ,handle type:{} method:{} patten:{} order:{}",j.toString(),wechatController.value(),method.getName(),wechatMapping.value(),wechatMapping.order());
                            ConcurrentMap<String,ConcurrentMap<Integer,Method>> methodMap = methodConcurrentMap.get(j);
                            if (methodMap == null){
                                methodMap = new ConcurrentHashMap();
                                methodConcurrentMap.put(j,methodMap);
                            }
                            ConcurrentMap<Integer,Method> realMethodMap = methodMap.get(wechatMapping.value());
                            if (realMethodMap == null){
                                realMethodMap = new ConcurrentHashMap<>();
                                String[] values = wechatMapping.value();
                                for (String value:values){
                                    methodMap.put(value,realMethodMap);
                                }
                            }
                            realMethodMap.put(wechatMapping.order(),method);
                        }
                    }
                }
            });
        });
        logger.info(" <====>  wechat executor init success");
    }

    /**
     * 处理文本消息
     */
    private Object handleText(List<Class> classList,Map<String,String> requestParam){
        String content = requestParam.get("Content");
        Map <Integer,PackageClass> temp = getHandleClass(classList,content);
        if (temp.isEmpty()){
            logger.warn("not found class to handle wechat message:{}",content);
        }else{
            return realHandle(temp,requestParam);
        }
        return WechatReturn.SUCCESS;
    }

    /**
     * 处理事件消息
     */
    private Object handleEvent(List<Class> classList,Map<String,String> requestParam){
        String patten = requestParam.get("Event");
        Map <Integer,PackageClass> temp = getHandleClass(classList,patten);
        if (temp.isEmpty()){
            logger.warn("not found class to handle wechat message:{}",patten);
        }else{
            return realHandle(temp,requestParam);
        }
        return WechatReturn.SUCCESS;
    }

    private Map <Integer,PackageClass> getHandleClass(List<Class> classList,String patten){
        //确定具体处理类
        Map <Integer,PackageClass> temp = new HashMap<>();
        for (Class clazz:classList){
            ConcurrentMap<String,ConcurrentMap<Integer,Method>> methodMap = methodConcurrentMap.get(clazz);
            if (methodMap != null){
                Iterator<String> keySet = methodMap.keySet().iterator();
                while (keySet.hasNext()){
                    //判定content是否匹配key
                    String keyStr = keySet.next();
                    if(Pattern.matches(keyStr,patten)){
                        ConcurrentMap<Integer,Method> realMethodMap = methodMap.get(keyStr);
                        Integer keyInteger = Collections.min(realMethodMap.keySet());
                        Method method = realMethodMap.get(keyInteger);
                        temp.put(keyInteger,new PackageClass(clazz,method));
                    }
                }
            }
        }
        return temp;
    }

    private Object realHandle(Map <Integer,PackageClass> temp,Map<String,String> requestParam){
        Integer keyInteger = Collections.min(temp.keySet());
        PackageClass packageClass = temp.get(keyInteger);
        return ReflectUtil.invoke(getBean(packageClass.getClazz()),packageClass.getMethod(),requestParam);
    }

    //通过class获取Bean.
    private <T> T getBean(Class<T> clazz) {
        return this.context.getBean(clazz);
    }


    class PackageClass{
        Class clazz;
        Method method;

        PackageClass(Class clazz,Method method){
            this.clazz = clazz;
            this.method = method;
        }

        public Class getClazz() {
            return clazz;
        }

        public void setClazz(Class clazz) {
            this.clazz = clazz;
        }

        public Method getMethod() {
            return method;
        }

        public void setMethod(Method method) {
            this.method = method;
        }
    }

}
