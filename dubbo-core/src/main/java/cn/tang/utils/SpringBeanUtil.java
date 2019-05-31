package cn.tang.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class SpringBeanUtil implements ApplicationContextAware {

    private static Logger logger = LoggerFactory.getLogger("SpringBeanUtil.class");

    private static ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringBeanUtil.applicationContext = applicationContext;
    }

    public SpringBeanUtil() {
        logger.info("SpringBeanUtil into spring");
    }

    /**
     * @param beanName
     * @return
     * @Title: getBeanByName
     * @Description: 通过bean的名字来获取Spring容器中的bean
     * @return: Object
     */
    public static Object getBeanByName(String beanName) {
        if (applicationContext == null) {
            return null;
        }
        return applicationContext.getBean(beanName);
    }

    public static <T> T getBean(Class<T> type) {
        return applicationContext.getBean(type);
    }

}