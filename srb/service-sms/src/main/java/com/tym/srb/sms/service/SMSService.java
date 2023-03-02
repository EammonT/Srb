package com.tym.srb.sms.service;

import java.util.Map;

public interface SMSService {
    void send(String mobile, Map<String ,Object> param);
}
