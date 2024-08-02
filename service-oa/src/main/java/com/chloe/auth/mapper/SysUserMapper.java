package com.chloe.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chloe.model.system.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户表 Mapper 接口
 * @author chloe
 * @since 2024-07-06
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

}
