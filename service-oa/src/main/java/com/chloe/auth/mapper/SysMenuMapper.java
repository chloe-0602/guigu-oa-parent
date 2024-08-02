package com.chloe.auth.mapper;

import com.chloe.model.system.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author chloe
 * @since 2024-07-07
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> findListByUserId(@Param("userId") Long userId);
}
