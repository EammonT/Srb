package com.tym.srb.core.controller.admin;

import com.tym.common.exception.Assert;
import com.tym.common.result.R;
import com.tym.common.result.ResponseEnum;
import com.tym.srb.core.pojo.entity.IntegralGrade;
import com.tym.srb.core.service.IntegralGradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin/core/integralGrade")
//@CrossOrigin
@Api(tags = "积分等级管理")
@Slf4j
public class AdminIntegralGradeController {

    @Resource
    private IntegralGradeService integralGradeService;

    @ApiOperation("积分等级列表")
    @GetMapping("/list")
    public R listAll(){
        log.info("hi this is log info");
        log.warn("hi this is log warn");
        log.error("hi this is log error");
        
        List<IntegralGrade> list = integralGradeService.list();
        return R.ok().data("list",list).message("获取列表成功");
    }

    @DeleteMapping("/remove/{id}")
    public R removeById(@PathVariable Long id){
        boolean result = integralGradeService.removeById(id);
        if (result){
            return R.ok().message("删除成功");
        }else {
            return R.error().message("删除失败");
        }
    }

    @ApiOperation("新增积分等级")
    @PostMapping("/save")
    public R save(@ApiParam(value = "积分等级对象",required = true) @RequestBody IntegralGrade integralGrade){
        boolean result = integralGradeService.save(integralGrade);
        Assert.notNull(integralGrade.getBorrowAmount(),ResponseEnum.BORROW_AMOUNT_NULL_ERROR);
        if (result){
            return R.ok().message("保存成功");
        }else {
            return R.error().message("保存失败");
        }
    }

    @ApiOperation("根据id获取积分等级")
    @GetMapping("/get/{id}")
    public R getById(@ApiParam(value = "积分等级对象", required = true) @PathVariable Long id){
        IntegralGrade integralGrade = integralGradeService.getById(id);
        if (integralGrade != null){
            return R.ok().data("record", integralGrade);
        }else {
            return R.error().message("获取数据失败");
        }
    }

    @ApiOperation("根据id修改积分等级")
    @PutMapping("/update")
    public R update(@ApiParam(value = "积分等级对象", required = true) @RequestBody IntegralGrade integralGrade){
        boolean result = integralGradeService.updateById(integralGrade);
        if (result){
            return R.ok().message("更新成功");
        }else {
            return R.error().message("更新失败");
        }
    }
}
