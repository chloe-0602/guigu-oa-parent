package com.chloe.wechat.service;

import com.chloe.model.wechat.Menu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chloe.vo.wechat.MenuVo;

import java.util.List;

/**
 * <p>
 * 菜单 服务类
 * </p>
 *
 * @author chloe
 * @since 2024-07-31
 */
public interface MenuService extends IService<Menu> {

    List<MenuVo> findMenuInfo();

    void syncMenu();

    void removeMenu();

}
