package com.nlife.wechat.msg.util;

import com.nlife.wechat.msg.constants.MsgType;
import com.nlife.wechat.msg.constants.WechatReturn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.helpers.AttributesImpl;

import javax.xml.transform.Result;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

/**
 * @Author nlife
 * @Date 2020/5/18
 * @Email xiamisspan@163.com
 */
public class ImgMsgWriter {

    private static Logger logger = LoggerFactory.getLogger(ImgMsgWriter.class);

    public static  String getXmlString(String toUserName,String fromUserName,String content){
        try{
            StringWriter writerStr = new StringWriter();
            Result resultXml = new StreamResult(writerStr);
            AttributesImpl attr = new AttributesImpl();
            TransformerHandler th = BaseMsgWriter.getxml(toUserName, fromUserName,attr);

            th.setResult(resultXml);

            th.startElement("", "", "MsgType", attr); //定义MsgType节点
            th.startCDATA();
            th.characters(MsgType.IMAGE.toCharArray(), 0, MsgType.IMAGE.length());
            th.endCDATA();
            th.endElement("", "", "MsgType"); //结束MsgType节点


            th.startElement("", "", "Image", attr);
            th.startElement("", "", "MediaId", attr);
            th.startCDATA();
            th.characters(content.toCharArray(), 0, content.length());
            th.endCDATA();
            th.endElement("", "", "MediaId");
            th.endElement("", "", "Image");


            th.endElement("", "", "xml"); //结束xml节点
            th.endDocument(); //结束xml文档

            return writerStr.getBuffer().toString();
        }catch (Exception e){
            logger.error("package result thows exception:{}",e.toString());
            return WechatReturn.SUCCESS;
        }

    }
}
