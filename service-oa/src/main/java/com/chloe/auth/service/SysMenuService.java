package com.chloe.auth.service;

import com.chloe.model.system.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chloe.vo.system.AssginMenuVo;
import com.chloe.vo.system.AssginRoleVo;
import com.chloe.vo.system.RouterVo;

import java.util.List;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author chloe
 * @since 2024-07-07
 */
public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> findNodes();

    List<SysMenu> findSysMenuByRoleId(Long roleId);

    void doAssign(AssginMenuVo assignMenuVo);

    List<RouterVo> findUserMenuList(Long userId);

    List<String> findUserPermsList(Long userId);
}
