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
 * @Date 2020/5/18
 * @Email xiamisspan@163.com
 */
public class TextMsgWriter {

    private static Logger logger = LoggerFactory.getLogger(TextMsgWriter.class);

    public static  String getXmlString(String toUserName,String fromUserName,String content){
        try{
            StringWriter writerStr = new StringWriter();
            Result resultXml = new StreamResult(writerStr);

            SAXTransformerFactory sff = (SAXTransformerFactory)SAXTransformerFactory.newInstance();
            TransformerHandler th = sff.newTransformerHandler();
            th.setResult(resultXml);

            Transformer transformer = th.getTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); //编码格式是UTF-8
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            th.startDocument(); //开始xml文档
            AttributesImpl attr = new AttributesImpl();
            BaseMsgWriter.packageXml(th,attr,toUserName,fromUserName);

            th.startElement("", "", "MsgType", attr); //定义MsgType节点
            th.startCDATA();
            th.characters(MsgType.TEXT.toCharArray(), 0, MsgType.TEXT.length());
            th.endCDATA();
            th.endElement("", "", "MsgType"); //结束MsgType节点


            th.startElement("", "", "Content", attr); //定义Content节点
            th.startCDATA();
            th.characters(content.toCharArray(), 0, content.length());
            th.endCDATA();
            th.endElement("", "", "Content"); //结束Content节点


            th.endElement("", "", "xml"); //结束xml节点
            th.endDocument(); //结束xml文档

            return writerStr.getBuffer().toString();
        }catch (Exception e){
            //组装微信返回文本信息抛出异常
            logger.error("组装微信返回文本信息抛出异常:{}",e.toString());
            return WechatReturn.SUCCESS;
        }

    }
}
