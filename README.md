### 介绍 

简单的处理微信推送框架

<font color=red>此框架只适用于spring项目</font>

### 使用帮助

1.引入WechatExecutor
```java
@Configuration
public class WechatConfig {

    @Bean
    public WechatExecutor wechatExecutor(){
        WechatExecutor wechatExecutor = new WechatExecutor();
        wechatExecutor.setScanPackages(new String[]{"com.test.weixin.handle"});
        return wechatExecutor;
    }
}
```
   2. 接收微信推送，交给WechatExecutor处理
```
@PostMapping
public Object receive(HttpServletRequest request, HttpServletResponse response){
    try{
        Map<String , String> map = WechatMessageUtil.parseXml(request);
        log.info("收到微信事件推送：{}", JSON.toJSONString(map));
        return wechatExecutor.handleRequest(map);
    }catch (Exception e){
        log.error(e.toString());
    }
    return WechatMessageUtil.SUCCESS;

}
```
3.新建handle类，具体处理对应的推送
```java
@WechatController(MsgType.TEXT)
@Component
@Slf4j
public class TextHandle {


    @WechatMapping(value = { ".*[你][好].*" , ".*[测][试].*" })
    public Object handleMsg(Map<String,String> param){
        return TextMsgWriter.getXmlString(param.get("FromUserName"),param.get("ToUserName"),"你也好啊");
    }
}


```
或者
```
@WechatController(MsgType.EVENT)
@Component
@Slf4j
public class EventHandle {

    @WechatMapping(EventType.SUBSCRIBE)
    public Object handleSubscribe(Map<String,String> param){
        return WechatReturn.SUCCESS;
    }
}
```
程序在接收到文本消息推送时，匹配到对应关键字时会自动调用handleMsg方法来处理
     
<font color=red>WechatMapping中value为正则表达式匹配，order为排序，多个匹配都成功时，order低的方法将被调用</font>




