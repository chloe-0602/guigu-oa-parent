package com.chloe.auth;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chloe.auth.mapper.SysRoleMapper;
import com.chloe.auth.service.SysRoleService;
import com.chloe.model.system.SysRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class TestMpSysRoleService {

    @Autowired
    private SysRoleService service;
    @Test
    public void testSelectRole(){
        List<SysRole> list = service.list(null);
        System.out.println(list);
    }

}
