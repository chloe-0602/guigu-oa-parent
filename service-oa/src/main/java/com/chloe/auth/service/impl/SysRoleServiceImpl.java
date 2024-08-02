package com.chloe.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chloe.auth.mapper.SysRoleMapper;
import com.chloe.auth.service.SysRoleService;
import com.chloe.auth.service.SysUserRoleService;
import com.chloe.model.system.SysRole;
import com.chloe.model.system.SysUserRole;
import com.chloe.vo.system.AssginRoleVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    @Autowired
    SysUserRoleService userRoleService;
    @Override
    public Map<String, Object> findRoleByUserId(Long userId) {

        List<SysRole> allRolesList = this.list();

        List<SysUserRole> existedUserRoleList = userRoleService.list(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getRoleId, userId));
        List<Long> existedRoleList = existedUserRoleList.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());

        List<SysRole> assignedRoleList = new ArrayList<>();
        allRolesList.stream().forEach(item -> {
            if (existedRoleList.contains(item)){
                assignedRoleList.add(item);
            }
        });

        Map<String, Object> roleMap = new HashMap<>();
        roleMap.put("assginRoleList", assignedRoleList);
        roleMap.put("allRolesList", allRolesList);

        return roleMap;
    }

    @Override
    public void doAssign(AssginRoleVo assginRoleVo) {
        userRoleService.remove(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, assginRoleVo.getUserId()));

        assginRoleVo.getRoleIdList().stream().forEach(roleId -> {
            if(roleId != null){
                SysUserRole sysUserRole = new SysUserRole();
                sysUserRole.setRoleId(roleId);
                sysUserRole.setUserId(assginRoleVo.getUserId());
                userRoleService.save(sysUserRole);
            }
        });
    }
}
