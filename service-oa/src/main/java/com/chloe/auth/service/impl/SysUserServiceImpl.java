package com.chloe.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chloe.auth.mapper.SysUserMapper;
import com.chloe.auth.service.SysMenuService;
import com.chloe.auth.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chloe.model.system.SysMenu;
import com.chloe.model.system.SysUser;
import com.chloe.security.custom.LoginUserInfoHelper;
import com.chloe.vo.system.RouterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 用户表 服务实现类
 * @author chloe
 * @since 2024-07-06
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Autowired
    private SysMenuService sysMenuService;

    @Transactional
    @Override
    public void updateStatus(Long id, Integer status) {
        SysUser sysUser = this.getById(id);
        if(status.intValue() == 1) {
            sysUser.setStatus(status);
        }else{
            sysUser.setStatus(0);
        }

        this.updateById(sysUser);
    }

    @Override
    public SysUser getByUserName(String userName) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, userName);
        return this.getOne(wrapper);
    }

    @Override
    public Map<String, Object> getUserInfo(String username) {
        Map<String, Object> result =  new HashMap<>();
        SysUser sysUser = this.getByUserName(username);
        List<RouterVo> routerVoList = sysMenuService.findUserMenuList(sysUser.getId());
        List<String> permsList = sysMenuService.findUserPermsList(sysUser.getId());

        result.put("name", sysUser.getName());
        result.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        //当前权限控制使用不到，我们暂时忽略
        result.put("roles",  new HashSet<>());
        result.put("buttons", permsList);
        result.put("routers", routerVoList);
        return result;
    }

    @Override
    public Map<String, Object> getCurrentUser() {
        SysUser sysUser = this.baseMapper.selectById(LoginUserInfoHelper.getUserId());
        //SysDept sysDept = sysDeptService.getById(sysUser.getDeptId());
        //SysPost sysPost = sysPostService.getById(sysUser.getPostId());
        Map<String, Object> map = new HashMap<>();
        map.put("name", sysUser.getName());
        map.put("phone", sysUser.getPhone());
        //map.put("deptName", sysDept.getName());
        //map.put("postName", sysPost.getName());
        return map;
    }
}
