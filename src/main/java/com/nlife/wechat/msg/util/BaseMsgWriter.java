package com.nlife.wechat.msg.util;

import com.nlife.wechat.msg.constants.MsgType;
import com.nlife.wechat.msg.constants.WechatReturn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

/**
 * @Author nlife
 * @Date 2020/5/29
 * @Email xiamisspan@163.com
 */
public class BaseMsgWriter {

    private static Logger logger = LoggerFactory.getLogger(ImgMsgWriter.class);

    public static  void packageXml(TransformerHandler th,AttributesImpl attr,String toUserName,String fromUserName){
        try{
            th.startDocument(); //开始xml文档
            th.startElement("", "", "xml", attr); //定义xml节点

            th.startElement("", "", "ToUserName", attr); //定义ToUserName节点
            th.startCDATA();
            th.characters(toUserName.toCharArray(), 0, toUserName.length());
            th.endCDATA();
            th.endElement("", "", "ToUserName"); //结束ToUserName节点

            th.startElement("", "", "FromUserName", attr); //定义FromUserName节点
            th.startCDATA();
            th.characters(fromUserName.toCharArray(), 0, fromUserName.length());
            th.endCDATA();
            th.endElement("", "", "FromUserName"); //结束FromUserName节点


            String createTime = System.currentTimeMillis()+"";

            th.startElement("", "", "CreateTime", attr); //定义CreateTime节点
            th.startCDATA();
            th.characters(createTime.toCharArray(), 0, createTime.length());
            th.endCDATA();
            th.endElement("", "", "CreateTime"); //结束CreateTime节点
        }catch (Exception e){
            logger.error("package result thows exception:{}",e.toString());
        }

    }

}
