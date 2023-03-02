package com.tym.srb.core.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "用户信息对象")
public class UserInfoVO {

    @ApiModelProperty("用户姓名")
    private String name;

    @ApiModelProperty("用户昵称")
    private String nickName;

    @ApiModelProperty("头像")
    private String headImg;

    @ApiModelProperty("手机号")
    private String mobile;

    @ApiModelProperty("1：出借人 2：借款人")
    private Integer userType;

    @ApiModelProperty("JWT访问令牌")
    private String token;
}
