package com.tym.srb.core.controller.api;


import com.alibaba.fastjson.JSON;
import com.tym.common.result.R;
import com.tym.srb.base.util.JwtUtils;
import com.tym.srb.core.hfb.RequestHelper;
import com.tym.srb.core.pojo.vo.UserBindVO;
import com.tym.srb.core.service.UserBindService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 用户绑定表 前端控制器
 * </p>
 *
 * @author tym
 * @since 2022-12-18
 */
@Api(tags = "会员账号绑定")
@Slf4j
@RestController
@RequestMapping("/api/core/userBind")
public class UserBindController {

    @Resource
    private UserBindService userBindService;

    @ApiOperation("账户绑定提交数据")
    @PostMapping("/auth/bind")
    public R bind(@RequestBody UserBindVO userBindVO, HttpServletRequest request){
        //从header中获取token，并对token进行校验，确保用户登录,获取userId
        String token = request.getHeader("token");
        Long userId = JwtUtils.getUserId(token);

        //根据userId做账户绑定,生成动态表单字符串
        String formStr = userBindService.commitBindUser(userBindVO,userId);
        return R.ok().data("formStr",formStr);
    }

    @ApiOperation("账户绑定异步回调")
    @PostMapping("/notify")
    public String notifyBack(HttpServletRequest request){
        Map<String, Object> paramMap = RequestHelper.switchMap(request.getParameterMap());
        log.info("账户绑定异步回调接收的参数：" + JSON.toJSONString(paramMap));
        if (!RequestHelper.isSignEquals(paramMap)){
            log.error("用户账户绑定异步回调签名验证错误"+JSON.toJSONString(paramMap));
            return "fail";
        }
        log.info("验签成功，开启账户绑定");
        userBindService.notify(paramMap);
        return "success";
    }
}

