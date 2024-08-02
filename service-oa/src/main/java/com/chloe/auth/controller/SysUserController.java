package com.chloe.auth.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chloe.auth.service.SysUserService;
import com.chloe.common.Result;
import com.chloe.model.system.SysUser;
import com.chloe.vo.system.SysUserQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户表 前端控制器
 * @author chloe
 * @since 2024-07-06
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/admin/system/sysUser")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;

    // 用户条件分页查询
    @ApiOperation("用户条件分页查询")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page,
                        @PathVariable Long limit,
                        SysUserQueryVo sysUserQueryVo) {

        IPage<SysUser> pageParams = new Page<>(page, limit);

        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        String usrnameKeyword = sysUserQueryVo.getKeyword();
        String createTimeBegin = sysUserQueryVo.getCreateTimeBegin();
        String createTimeEnd = sysUserQueryVo.getCreateTimeEnd();

        if(!StringUtils.isEmpty(usrnameKeyword)){
            wrapper.like(SysUser::getUsername, usrnameKeyword);
        }
        if(!StringUtils.isEmpty(createTimeBegin)){
            wrapper.ge(SysUser::getCreateTime, createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)){
            wrapper.le(SysUser::getCreateTime, createTimeEnd);
        }

        IPage<SysUser> pageModel = sysUserService.page(pageParams, wrapper);
        return Result.ok(pageModel);
    }

    @ApiOperation(value = "获取用户")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        return Result.ok(user);
    }

    @ApiOperation(value = "保存用户")
    @PostMapping("save")
    public Result save(@RequestBody SysUser user) {
        sysUserService.save(user);
        return Result.ok();
    }

    @ApiOperation(value = "更新用户")
    @PutMapping("update")
    public Result updateById(@RequestBody SysUser user) {
        sysUserService.updateById(user);
        return Result.ok();
    }

    @ApiOperation(value = "删除用户")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        sysUserService.removeById(id);
        return Result.ok();
    }
    @ApiOperation(value = "更新状态")
    @GetMapping("updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable Long id, @PathVariable Integer status) {
        sysUserService.updateStatus(id, status);
        return Result.ok();
    }
}

