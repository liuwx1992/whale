package com.zoo.whale.example.area;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class PropertiesUtil {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    private static Properties properties;

    static {
        properties = new Properties();
        try {
            InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream("hnconfig.properties");
            properties.load(in);
            in.close();
        } catch (IOException e) {
            logger.error("getClassloader Exception!", e);
        }
    }

    public static Properties getProperties() {
        return properties;
    }

    public static Integer getIntegerValue(String key) {
        return Integer.valueOf(properties.getProperty(key));
    }

    public static String getStringValue(String key) {
        return properties.getProperty(key);
    }

    public static boolean getBooleanValue(String key) {
        return Boolean.parseBoolean(properties.getProperty(key));
    }

    public static Object getValue(String key) {
        return properties.getProperty(key);
    }

  /*public static void main(String[] rags) {
        System.out.print(PropertiesUtil.getStringValue("jdbc.mysql.driverClass"));
    }*/
}
