package com.tym.srb.sms.service.impl;

import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.tym.srb.sms.service.SMSService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class SMSServiceImpl implements SMSService {
    @Value(value = "${rckj.accountSId}")
    private String ACCOUNTS_ID;
    @Value(value = "${rckj.accountToken}")
    private String ACCOUNTTOKEN;
    @Value(value = "${rckj.appId}")
    private String APPID;
    @Value(value = "${rckj.serverIp}")
    private String serverIp;
    @Value(value = "${rckj.serverPort}")
    private String serverPort;

    @Override
    public void send(String mobile, Map<String, Object> param) {
        CCPRestSmsSDK ccpRestSmsSDK=new CCPRestSmsSDK();
        ccpRestSmsSDK.setAccount(ACCOUNTS_ID,ACCOUNTTOKEN);
        ccpRestSmsSDK.setAppId(APPID);
        ccpRestSmsSDK.setBodyType(BodyType.Type_JSON);
        ccpRestSmsSDK.init(serverIp,serverPort);
        String[] datas={(String) param.get("code"),"2"};
        HashMap<String, Object> result = ccpRestSmsSDK.sendTemplateSMS(mobile, "1", datas);
        System.out.println("SDKTestGetSubAccounts result=" + result);
        if("000000".equals(result.get("statusCode"))){
            //正常返回输出data包体信息（map）
            HashMap<String,Object> data = (HashMap<String, Object>) result.get("data");
            Set<String> keySet = data.keySet();
            for(String key:keySet){
                Object object = data.get(key);
                System.out.println(key +" = "+object);
            }
        }else{
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
        }
    }
}
