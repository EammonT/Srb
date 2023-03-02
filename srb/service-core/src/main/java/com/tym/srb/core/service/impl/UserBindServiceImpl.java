package com.tym.srb.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tym.common.exception.Assert;
import com.tym.common.result.ResponseEnum;
import com.tym.srb.core.enums.UserBindEnum;
import com.tym.srb.core.hfb.FormHelper;
import com.tym.srb.core.hfb.HfbConst;
import com.tym.srb.core.hfb.RequestHelper;
import com.tym.srb.core.mapper.UserBindMapper;
import com.tym.srb.core.mapper.UserInfoMapper;
import com.tym.srb.core.pojo.entity.UserBind;
import com.tym.srb.core.pojo.entity.UserInfo;
import com.tym.srb.core.pojo.vo.UserBindVO;
import com.tym.srb.core.service.UserBindService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 服务实现类
 * </p>
 *
 * @author tym
 * @since 2022-12-18
 */
@Service
public class UserBindServiceImpl extends ServiceImpl<UserBindMapper, UserBind> implements UserBindService {

    @Resource
    private UserInfoMapper userInfoMapper;
    @Override
    public String commitBindUser(UserBindVO userBindVO, Long userId) {
        //不同的user_id相同的身份证不允许
        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("id_card",userBindVO.getIdCard())
                .ne("user_id",userId);
        UserBind userBind = baseMapper.selectOne(userBindQueryWrapper);
        Assert.isNull(userBind, ResponseEnum.USER_BIND_IDCARD_EXIST_ERROR);

        //用户是否填写过表单
        userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("user_id",userId);
        userBind = baseMapper.selectOne(userBindQueryWrapper);
        //创建用户绑定记录
        if (userBind == null){
            userBind = new UserBind();
            BeanUtils.copyProperties(userBindVO,userBind);
            userBind.setUserId(userId);
            userBind.setStatus(UserBindEnum.NO_BIND.getStatus());
            baseMapper.insert(userBind);
        }else {
            BeanUtils.copyProperties(userBindVO,userBind);
            baseMapper.updateById(userBind);
        }

        //组装自动提交表单的参数
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("agentId",HfbConst.AGENT_ID);
        paramMap.put("agentUserId",userId);
        paramMap.put("idCard",userBindVO.getIdCard());
        paramMap.put("personalName",userBindVO.getName());
        paramMap.put("bankType", userBindVO.getBankType());
        paramMap.put("bankNo", userBindVO.getBankNo());
        paramMap.put("mobile", userBindVO.getMobile());
        paramMap.put("returnUrl", HfbConst.USERBIND_RETURN_URL);
        paramMap.put("notifyUrl", HfbConst.USERBIND_NOTIFY_URL);
        paramMap.put("timestamp", RequestHelper.getTimestamp());
        paramMap.put("sign", RequestHelper.getSign(paramMap));
        //生成动态表单字符串
        String formStr = FormHelper.buildForm(HfbConst.USERBIND_URL,paramMap);
        return formStr;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notify(Map<String, Object> paramMap) {
        String bindCode = (String) paramMap.get("bindCode");
        String userId = (String) paramMap.get("agentUserId");
        QueryWrapper<UserBind> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        UserBind userBind = baseMapper.selectOne(queryWrapper);
        userBind.setBindCode(bindCode);
        userBind.setStatus(UserBindEnum.BIND_OK.getStatus());
        baseMapper.updateById(userBind);
        System.out.println("===========================");
        UserInfo userInfo = userInfoMapper.selectById(bindCode);
        userInfo.setBindCode(bindCode);
        userInfo.setName(userBind.getName());
        userInfo.setIdCard(userBind.getIdCard());
        userInfo.setBindStatus(UserBindEnum.BIND_OK.getStatus());
        userInfoMapper.updateById(userInfo);

    }

    @Override
    public String getBindCodeByUserId(Long userId) {

        QueryWrapper<UserBind> userBindQueryWrapper = new QueryWrapper<>();
        userBindQueryWrapper.eq("user_id",userId);
        UserBind userBind = baseMapper.selectOne(userBindQueryWrapper);
        return userBind.getBindCode();
    }
}
