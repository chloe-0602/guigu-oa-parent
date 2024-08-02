package com.chloe.auth.service.impl;

import com.chloe.auth.service.SysMenuService;
import com.chloe.auth.service.SysUserService;
import com.chloe.model.system.SysUser;
import com.chloe.security.custom.CustomUser;
import com.chloe.security.custom.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysMenuService sysMenuService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser sysUser = sysUserService.getByUserName(username);
        if(null == sysUser) {
            throw new UsernameNotFoundException("用户名不存在！");
        }

        if(sysUser.getStatus().intValue() == 0) {
            throw new RuntimeException("账号已停用");
        }

        List<String> userPermsList = sysMenuService.findUserPermsList(sysUser.getId());
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();

        for (String perm : userPermsList) {
            authorities.add(new SimpleGrantedAuthority(perm.trim()));
        }

        return new CustomUser(sysUser, authorities);
    }
}
