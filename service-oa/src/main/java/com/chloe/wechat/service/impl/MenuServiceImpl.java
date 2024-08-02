package com.chloe.wechat.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.chloe.model.wechat.Menu;
import com.chloe.vo.wechat.MenuVo;
import com.chloe.wechat.config.WeChatMpConfig;
import com.chloe.wechat.mapper.MenuMapper;
import com.chloe.wechat.service.MenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单 服务实现类
 * </p>
 *
 * @author chloe
 * @since 2024-07-31
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private WxMpService wifiMpService;

    @Override
    public List<MenuVo> findMenuInfo() {

        List<MenuVo> list = new ArrayList<>();

        List<Menu> menuList = menuMapper.selectList(null);
        List<Menu> oneMenuList = menuList.stream()
                .filter(menu -> menu.getParentId().longValue() == 0)
                .collect(Collectors.toList());

        for (Menu oneMenu: oneMenuList) {

            MenuVo menuVo = new MenuVo();
            BeanUtils.copyProperties(oneMenu, menuVo);
            List<Menu> twoMenuList = menuList.stream()
                    .filter(menu -> menu.getParentId().longValue() == oneMenu.getId())
                    .sorted(Comparator.comparing(Menu::getSort))
                    .collect(Collectors.toList());

            List<MenuVo> children = new ArrayList<>();
            for (Menu twoMenu: twoMenuList) {
                MenuVo twoMenuVo = new MenuVo();
                BeanUtils.copyProperties(twoMenu, twoMenuVo);
                children.add(twoMenuVo);
            }

            menuVo.setChildren(children);
            list.add(menuVo);
        }
        return list;
    }

    @Override
    public void syncMenu() {
        List<MenuVo> menuVoList = this.findMenuInfo();
        //菜单
        JSONArray buttonList = new JSONArray();
        for(MenuVo oneMenuVo : menuVoList) {
            JSONObject one = new JSONObject();
            one.put("name", oneMenuVo.getName());
            if(CollectionUtils.isEmpty(oneMenuVo.getChildren())) {
                one.put("type", oneMenuVo.getType());
                one.put("url", "http://chloe2.v7.idcfengye.com/#"+oneMenuVo.getUrl());
            } else {
                JSONArray subButton = new JSONArray();
                for(MenuVo twoMenuVo : oneMenuVo.getChildren()) {
                    JSONObject view = new JSONObject();
                    view.put("type", twoMenuVo.getType());
                    if(twoMenuVo.getType().equals("view")) {
                        view.put("name", twoMenuVo.getName());
                        //H5页面地址
                        view.put("url", "http://chloe2.v7.idcfengye.com#"+twoMenuVo.getUrl());
                    } else {
                        view.put("name", twoMenuVo.getName());
                        view.put("key", twoMenuVo.getMeunKey());
                    }
                    subButton.add(view);
                }
                one.put("sub_button", subButton);
            }
            buttonList.add(one);
        }

        JSONObject button = new JSONObject();
        button.put("button", buttonList);
        try {
            wifiMpService.getMenuService().menuCreate(button.toJSONString());
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeMenu() {
        try {
            wifiMpService.getMenuService().menuDelete();
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }
}
