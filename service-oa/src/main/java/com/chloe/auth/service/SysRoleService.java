package com.chloe.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.chloe.model.system.SysRole;
import com.chloe.vo.system.AssginRoleVo;

import java.util.Map;

public interface SysRoleService extends IService<SysRole> {
    Map<String, Object> findRoleByUserId(Long userId);

    void doAssign(AssginRoleVo assginRoleVo);
}
