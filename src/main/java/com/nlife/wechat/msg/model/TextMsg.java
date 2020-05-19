package com.nlife.wechat.msg.model;


/**
 * @Author nlife
 * @Date 2020/5/15
 * @Email xiamisspan@163.com
 */
public class TextMsg extends BaseMsg {
    /**
     * 文本消息内容
     */
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
