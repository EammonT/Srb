package com.tym.srb.core.pojo.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "账户绑定")
public class UserBindVO {

    @ApiModelProperty("身份证号")
    private String idCard;

    @ApiModelProperty("用户姓名")
    private String name;

    @ApiModelProperty("银行类型")
    private String bankType;

    @ApiModelProperty("银行卡号")
    private String bankNo;

    @ApiModelProperty("手机号")
    private String mobile;
}
