
package cn.tang.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * Created by wangqi on 2016/4/24.
 */

public class ReadProperties {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(ReadProperties.class);

    /**
     * @param propertiesFilename
     * @param property
     * @param vm
     * @return
     */
    public static String ReadPropertiesFromfiles(String propertiesFilename, String property, String vm) {
        Properties props = System.getProperties(); //系统属性
        String confHome = props.getProperty(vm);
        logger.info(vm + ":" + confHome);
        Properties prop = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream(confHome + propertiesFilename);
            if ("/api.properties".equals(propertiesFilename)) {
                prop.load(new InputStreamReader(in, "UTF-8"));
            } else {
                prop.load(in);
            }
            String value = prop.getProperty(property);
            logger.info(value);
            in.close();
            return value;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
