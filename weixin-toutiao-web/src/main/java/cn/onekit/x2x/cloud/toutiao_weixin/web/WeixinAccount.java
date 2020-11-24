package cn.onekit.x2x.cloud.toutiao_weixin.web;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = {"classpath:config.properties"},ignoreResourceNotFound = true)
public class WeixinAccount implements ApplicationContextAware {

    static String wx_appid;
    static String wx_secret;



    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        wx_appid = applicationContext.getEnvironment().getProperty("weixin.appid");
        wx_secret = applicationContext.getEnvironment().getProperty("weixin.secret");

    }
}
