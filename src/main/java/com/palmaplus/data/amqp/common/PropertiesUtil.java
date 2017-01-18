package com.palmaplus.data.amqp.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by jiabing.zhu on 2016/10/8.
 */
public class PropertiesUtil {
    private final static Properties properries = new Properties();
    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
    static {
        //读取本地配置
//       readProperty("localProperties.properties");
        //读取服务器相关配置
        readProperty("properties.properties");
    }
    private static void readProperty(String filename){
        try {
            InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream(filename);
            properries.load(in);
        } catch (IOException e){
            logger.debug("read properties exception : " + e.getMessage());
        }
    }
    /**
     * 获取某个属性
     */
    public static String getProperty(String key){
        return properries.getProperty(key);
    }

    public static void main(String[] args) {
        System.out.println(getProperty("{\"uid\":\"735809317\",\"youmengId\":\"564a8cb8e0f55ad413001489\",\"timeStatmp\":\"4744006524089843801\",\"clientId\":\"\",\"sourceId\":\"203007\",\"token\":\"\",\"appType\":\"3\"}"));
    }
}
