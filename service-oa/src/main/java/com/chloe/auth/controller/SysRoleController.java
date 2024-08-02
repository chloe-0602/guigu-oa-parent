package com.chloe.auth.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chloe.auth.service.SysRoleService;
import com.chloe.common.Result;
import com.chloe.common.exception.OaException;
import com.chloe.model.system.SysRole;
import com.chloe.vo.system.AssginRoleVo;
import com.chloe.vo.system.SysRoleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(tags = "角色管理")
@RestController
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {
    @Autowired
    private SysRoleService roleService;

    @Autowired
    private SysRoleService sysRoleService;
    @ApiOperation(value = "获取全部角色列表")
    @GetMapping("findAll")
    public Result findAll(){
        List<SysRole> sysRoles = roleService.list();

//        try {
//            int res = 5 / 0;
//        } catch (Exception e) {
//            throw new OaException(20001, "出现自定义异常......");
//        }
        return Result.ok(sysRoles);
    }

    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation(value = "条件分页查询")
    @GetMapping("{currPage}/{pageSize}")
    public Result pageQuery(@PathVariable Long currPage,
                            @PathVariable Long pageSize,
                            SysRoleQueryVo sysRoleQueryVo){

        Page<SysRole> page = new Page<>(currPage, pageSize);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        String roleName = sysRoleQueryVo.getRoleName();
        if(!StringUtils.isEmpty(roleName)){
            wrapper.like(SysRole::getRoleName, roleName);
        }

        IPage<SysRole> pageModel = roleService.page(page, wrapper);

        return Result.ok(pageModel);
    }

    // 根据id获取角色
    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation("根据id获取角色")
    @GetMapping("get/{id}")
    public Result getById(@PathVariable Long id){
        SysRole sysRole = roleService.getById(id);
        return Result.ok(sysRole);
    }

    // 添加角色
    @PreAuthorize("hasAuthority('bnt.sysRole.add')")
    @ApiOperation("添加角色")
    @PostMapping("save")
    public Result save(@RequestBody SysRole role){
        boolean isSuccess = roleService.save(role);
        if(isSuccess){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    // 修改角色
    @PreAuthorize("hasAuthority('bnt.sysRole.update')")
    @ApiOperation("修改角色")
    @PutMapping("update")
    public Result update(@RequestBody SysRole role){
        boolean isSuccess = roleService.updateById(role);
        if(isSuccess){
            return Result.ok();
        }else{
            return Result.fail();
        }
    }

    // 根据id删除角色
    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @ApiOperation("根据id删除角色")
    @DeleteMapping("remove/{id}")
    public Result removeById(@PathVariable Long id) {
        boolean isSuccess = roleService.removeById(id);
        if (isSuccess) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }
    // 根据id批量删除角色
    @ApiOperation("根据id删除角色")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        boolean isSuccess = roleService.removeByIds(idList);
        if (isSuccess) {
            return Result.ok();
        } else {
            return Result.fail();
        }
    }

    @ApiOperation(value = "根据用户获取角色数据")
    @GetMapping("/toAssign/{userId}")
    public Result toAssign(@PathVariable Long userId) {
        Map<String, Object> roleMap = sysRoleService.findRoleByUserId(userId);
        return Result.ok(roleMap);
    }

    @ApiOperation(value = "根据用户分配角色")
    @PostMapping("/doAssign")
    public Result doAssign(@RequestBody AssginRoleVo assginRoleVo) {
        sysRoleService.doAssign(assginRoleVo);
        return Result.ok();
    }

}
