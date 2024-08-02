package com.chloe.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chloe.auth.mapper.SysMenuMapper;
import com.chloe.auth.mapper.SysRoleMenuMapper;
import com.chloe.auth.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chloe.auth.service.SysRoleService;
import com.chloe.common.exception.GuiguException;
import com.chloe.model.system.SysMenu;
import com.chloe.model.system.SysRoleMenu;
import com.chloe.vo.system.AssginMenuVo;
import com.chloe.vo.system.AssginRoleVo;
import com.chloe.vo.system.MetaVo;
import com.chloe.vo.system.RouterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.chloe.auth.utils.MenuHelper;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author chloe
 * @since 2024-07-07
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Override
    public List<SysMenu> findNodes() {

        List<SysMenu> sysMenuList = this.list();
        if(CollectionUtils.isEmpty(sysMenuList)){
            return null;
        }

        List<SysMenu> res = MenuHelper.buildTree(sysMenuList);
        return res;
    }

    @Override
    public List<SysMenu> findSysMenuByRoleId(Long roleId) {
        //全部权限列表
        List<SysMenu> allSysMenuList = this.list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getStatus, 1));

        //根据角色id获取角色权限
        List<SysRoleMenu> sysRoleMenuList = sysRoleMenuMapper.selectList(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        //转换给角色id与角色权限对应Map对象
        List<Long> menuIdList = sysRoleMenuList.stream().map(e -> e.getMenuId()).collect(Collectors.toList());

        allSysMenuList.forEach(permission -> {
            if (menuIdList.contains(permission.getId())) {
                permission.setSelect(true);
            } else {
                permission.setSelect(false);
            }
        });

        List<SysMenu> sysMenuList = MenuHelper.buildTree(allSysMenuList);
        return sysMenuList;
    }

    @Override
    public void doAssign(AssginMenuVo assignMenuVo) {
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, assignMenuVo.getRoleId()));

        for (Long menuId : assignMenuVo.getMenuIdList()) {
            if (StringUtils.isEmpty(menuId)) continue;
            SysRoleMenu rolePermission = new SysRoleMenu();
            rolePermission.setRoleId(assignMenuVo.getRoleId());
            rolePermission.setMenuId(menuId);
            sysRoleMenuMapper.insert(rolePermission);
        }
    }

    @Override
    public List<RouterVo> findUserMenuList(Long userId) {

        List<SysMenu> sysMenuList = null;
        if(userId.longValue() == 1){
            // 系统管理员
            sysMenuList = this.list(new LambdaQueryWrapper<SysMenu>()
                    .eq(SysMenu::getStatus, 1)
                    .orderByAsc(SysMenu::getSortValue)
            );

        }else {
            sysMenuList = sysMenuMapper.findListByUserId(userId);
        }

        List<SysMenu> sysMenyTreeList = MenuHelper.buildTree(sysMenuList);
        List<RouterVo> routerVoList = this.buildMenuRouterList(sysMenyTreeList);
        return routerVoList;
    }

    private List<RouterVo> buildMenuRouterList(List<SysMenu> sysMenyTreeList) {
        List<RouterVo> routers = new LinkedList<>();
        for (SysMenu menu: sysMenyTreeList) {
            RouterVo routerVo = new RouterVo();
            routerVo.setPath(getRouterPath(menu));
            routerVo.setHidden(false);
            routerVo.setComponent(menu.getComponent());
            routerVo.setMeta(new MetaVo(menu.getName(), menu.getIcon()));

            List<SysMenu> children = menu.getChildren();
            //如果当前是菜单，需将按钮对应的路由加载出来，如：“角色授权”按钮对应的路由在“系统管理”下面
            if(menu.getType().intValue() == 1){
                List<SysMenu> hiddenMenuList = children.stream()
                        .filter(item -> !StringUtils.isEmpty(item.getComponent()))
                        .collect(Collectors.toList());

                for (SysMenu hiddenMenu : hiddenMenuList) {
                    RouterVo hiddenRouter = new RouterVo();
                    hiddenRouter.setHidden(true);
                    hiddenRouter.setAlwaysShow(false);
                    hiddenRouter.setPath(getRouterPath(hiddenMenu));
                    hiddenRouter.setComponent(hiddenMenu.getComponent());
                    hiddenRouter.setMeta(new MetaVo(hiddenMenu.getName(), hiddenMenu.getIcon()));
                    routers.add(hiddenRouter);
                }
            }else {
                // 非隐藏目录
                if(!CollectionUtils.isEmpty(children)){
                    if(children.size() > 0){
                        routerVo.setAlwaysShow(true);
                    }
                }
                routerVo.setChildren(buildMenuRouterList(children));
            }

            routers.add(routerVo);

        }
        return routers;
    }

    private String getRouterPath(SysMenu menu) {
        String routerPath = "/" + menu.getPath();
        if(menu.getParentId().longValue() != 0){
            routerPath = menu.getPath();
        }
        return routerPath;
    }

    @Override
    public List<String> findUserPermsList(Long userId) {
        List<SysMenu> sysMenuList = null;
        if(userId.longValue() == 1){
            // 系统管理员，拥有所有菜单权限
            sysMenuList = this.list(new LambdaQueryWrapper<SysMenu>()
                    .eq(SysMenu::getStatus, 1));
        }else {
            sysMenuList = sysMenuMapper.findListByUserId(userId);
        }

        List<String> permsList = sysMenuList.stream()
                .filter(item -> item.getType() == 2)
                .map(SysMenu::getPerms)
                .collect(Collectors.toList());
        return permsList;
    }

    @Override
    public boolean removeById(Serializable id) {
        int count = this.count(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, id));
        if (count > 0) {
            throw new GuiguException(201,"菜单不能删除");
        }
        sysMenuMapper.deleteById(id);
        return false;
    }

}
