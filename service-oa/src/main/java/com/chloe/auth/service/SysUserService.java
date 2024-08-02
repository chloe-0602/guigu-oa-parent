package com.chloe.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chloe.model.system.SysUser;

import java.util.Map;

/**
 * 用户表 服务类
 *
 * @author chloe
 * @since 2024-07-06
 */
public interface SysUserService extends IService<SysUser> {
    void updateStatus(Long id, Integer status);
    SysUser getByUserName(String userName);

    Map<String, Object> getUserInfo(String username);

    Map<String, Object> getCurrentUser();
}
