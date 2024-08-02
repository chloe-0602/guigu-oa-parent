package com.chloe.auth.utils;

import com.chloe.model.system.SysMenu;

import java.util.ArrayList;
import java.util.List;

public class MenuHelper {
    public static List<SysMenu> buildTree(List<SysMenu> sysMenuList) {

        List<SysMenu> tree = new ArrayList<>();
        for (SysMenu sysMenu : sysMenuList) {
            if(sysMenu.getParentId().longValue() == 0){
                tree.add(findChildren(sysMenu, sysMenuList));
            }
        }

        return tree;
    }

    private static SysMenu findChildren(SysMenu sysMenu, List<SysMenu> treeNodes) {
        sysMenu.setChildren(new ArrayList<SysMenu>());

        // sysMeun 当前的
        // it      是需要看的
        for (SysMenu it : treeNodes) {
            if(sysMenu.getId().longValue() == it.getParentId().longValue()){
                // 初始化
                if(sysMenu.getChildren() == null){
                    sysMenu.setChildren(new ArrayList<>());
                }

                sysMenu.getChildren().add(findChildren(it, treeNodes));
            }
        }
        return sysMenu;
    }
}
