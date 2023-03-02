package com.tym.common.util;

import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "rckj")
@Data
public class SMSProperties implements InitializingBean {

    private String accountSId;
    private String accountToken;
    private String appId;
    private String serverIp;
    private Long serverPort;
    private String templateCode;

    public static String ACCOUNT_SID;
    public static String ACCOUNT_TOKEN;
    public static String APP_ID;
    public static String SERVER_IP;
    public static Long SERVER_PORT;
    public static String TEMPLATE_CODE;


    @Override
    public void afterPropertiesSet() throws Exception {
        this.ACCOUNT_SID = accountSId;
        this.ACCOUNT_TOKEN = accountToken;
        this.APP_ID = appId;
        this.SERVER_IP = serverIp;
        this.SERVER_PORT = serverPort;
        this.TEMPLATE_CODE = templateCode;
    }
}
