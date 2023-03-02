package com.tym.srb.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tym.srb.core.pojo.entity.UserInfo;
import com.tym.srb.core.pojo.query.UserInfoQuery;
import com.tym.srb.core.pojo.vo.LoginVO;
import com.tym.srb.core.pojo.vo.RegisterVO;
import com.tym.srb.core.pojo.vo.UserIndexVO;
import com.tym.srb.core.pojo.vo.UserInfoVO;

/**
 * <p>
 * 用户基本信息 服务类
 * </p>
 *
 * @author tym
 * @since 2022-12-18
 */
public interface UserInfoService extends IService<UserInfo> {

    void register(RegisterVO registerVO);

    UserInfoVO login(LoginVO loginVO,String ip);


    IPage<UserInfo> listPage(Page<UserInfo> pageParam, UserInfoQuery userInfoQuery);

    void lock(Long id,Integer status);

    boolean checkMobile(String mobile);

    UserIndexVO getIndexUserInfo(Long userId);

    String getMobileByBindCode(String bindCode);
}
