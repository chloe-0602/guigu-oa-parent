package com.chloe.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chloe.auth.mapper.SysRoleMapper;
import com.chloe.model.system.SysRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class TestMpSysRole {

    @Autowired
    private SysRoleMapper roleMapper;
    @Test
    public void testSelectRole(){
        List<SysRole> list = roleMapper.selectList(null);
        System.out.println(list);
    }

    @Test
    public void testAddRole(){
        SysRole sysRole = new SysRole();
        sysRole.setRoleName("角色管理员2");
        sysRole.setRoleCode("admin");
        sysRole.setDescription("this is desc");

        int res = roleMapper.insert(sysRole);

        System.out.println(res);
        System.out.println(sysRole);

    }
    @Test
    public void testUpdateRole(){
        SysRole sysRole = roleMapper.selectById(9);
        sysRole.setRoleName("角色管理员update");

        int row = roleMapper.updateById(sysRole);

        System.out.println(sysRole);
        System.out.println(row);
    }

    @Test
    public void testDeleteRole(){

        int row = roleMapper.deleteById(9);

        System.out.println(row);

    }

    @Test
    public void testDeleteBatchRole(){

        List<SysRole> sysRoles = roleMapper.selectList(null);
        System.out.println("before delete: " + sysRoles);
        int row = roleMapper.deleteBatchIds(Arrays.asList(10, 11));

        System.out.println(row);
        sysRoles = roleMapper.selectList(null);
        System.out.println("after delete: " + sysRoles);
    }

    @Test
    public void testselectByQueryWrapper(){

        QueryWrapper<SysRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_name", "角色管理员1");

        List<SysRole> sysRoles = roleMapper.selectList(queryWrapper);
        System.out.println(sysRoles);
    }

    @Test
    public void testselectByLambdaQueryWrapper(){

        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleName, "角色管理员1");

        List<SysRole> sysRoles = roleMapper.selectList(wrapper);
        System.out.println(sysRoles);
    }
}
