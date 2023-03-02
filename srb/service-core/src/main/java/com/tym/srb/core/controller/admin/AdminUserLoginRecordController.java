package com.tym.srb.core.controller.admin;


import com.tym.common.result.R;
import com.tym.srb.core.pojo.entity.UserLoginRecord;
import com.tym.srb.core.service.UserLoginRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用户登录记录表 前端控制器
 * </p>
 *
 * @author tym
 * @since 2022-12-18
 */
@Api(tags = "会员登录日志接口")
@Slf4j
//@CrossOrigin
@RestController
@RequestMapping("/admin/core/userLoginRecord")
public class AdminUserLoginRecordController {

    @Resource
    private UserLoginRecordService userLoginRecordService;

    @ApiOperation("获取会员登录日志列表")
    @GetMapping("/listTop50/{userId}")
    public R listTop50(
            @ApiParam(value = "用户id" ,required = true)
            @PathVariable Long userId
    ){
        List<UserLoginRecord> userLoginRecords = userLoginRecordService.listTop50(userId);
        return R.ok().data("list",userLoginRecords);
    }

}

