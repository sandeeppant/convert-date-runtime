package com.kronos.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationMap
{
    private static Properties prop1;
    private static Properties prop2;
    private static Properties prop3;
    private static Properties prop4;
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationMap.class);

    static
    {
        prop1 = new Properties();
        prop2 = new Properties();
        prop3 = new Properties();
        prop4 = new Properties();
        InputStream is1 = null;
        InputStream is2 = null;
        InputStream is3 = null;
        InputStream is4 = null;
        try
        {
            is1 = ConfigurationMap.class.getClassLoader().getResourceAsStream("date_new_1.properties");
            prop1.load(is1);
            is2 = ConfigurationMap.class.getClassLoader().getResourceAsStream("date_new_2.properties");
            prop2.load(is2);
            is3 = ConfigurationMap.class.getClassLoader().getResourceAsStream("date_new_3.properties");
            prop3.load(is3);
            is4 = ConfigurationMap.class.getClassLoader().getResourceAsStream("config.properties");
            prop4.load(is4);
        }
        catch (IOException e)
        {
            LOGGER.error("Error occoured while loading properties: " + e);
        }
        finally
        {
            try
            {
                is1.close();
                is2.close();
                is3.close();
                is4.close();
            }
            catch (IOException e)
            {
                LOGGER.error("Error while closing the connection");
            }
        }
        LOGGER.info("loadConfigurations: Loading properties file");
    }

    public static Set<Object> getAllKeys1(){
        Set<Object> keys = prop1.keySet();
        return keys;
    }
    
    public static Set<Object> getAllKeys2(){
        Set<Object> keys = prop2.keySet();
        return keys;
    }

    public static Set<Object> getAllKeys3(){
        Set<Object> keys = prop3.keySet();
        return keys;
    }
    
    public static Set<Object> getAllkeys()
    {
        Set<Object> keys = new HashSet<Object>();
        keys.addAll(getAllKeys1());
        keys.addAll(getAllKeys2());
        keys.addAll(getAllKeys3());
        return keys;
    }
    
    public static String getPropertyValue1(String key)
    {
        return prop1.getProperty(key);
    }

    public static String getPropertyValue2(String key)
    {
        return prop2.getProperty(key);
    }

    public static String getPropertyValue3(String key)
    {
        return prop3.getProperty(key);
    }
    public static String getPropertyValue4(String key)
    {
        return prop4.getProperty(key);
    }
}
