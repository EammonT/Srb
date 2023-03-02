package com.tym.srb.sms;

import com.tym.common.util.SMSProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UtilsTest {
    @Test
    public void testProperties(){
        System.out.println(SMSProperties.ACCOUNT_SID);
        System.out.println(SMSProperties.ACCOUNT_TOKEN);
        System.out.println(SMSProperties.APP_ID);
        System.out.println(SMSProperties.SERVER_IP);
        System.out.println(SMSProperties.SERVER_PORT);
    }
}
