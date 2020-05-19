package com.nlife.wechat.msg.constants;

/**
 * @Author nlife
 * @Date 2020/5/18
 * @Email xiamisspan@163.com
 */
public interface EventType {

    /**关注事件*/
    String SUBSCRIBE = "subscribe";

    /**取消关注事件*/
    String UNSUBSCRIBE = "unsubscribe";

    /**扫描二维码事件*/
    String SCAN = "SCAN";

    /**上报地理位置事件*/
    String LOCATION = "LOCATION";

    /**点击自定义菜单事件*/
    String CLICK = "CLICK";

    /**跳转网页事件*/
    String VIEW = "VIEW";
}
