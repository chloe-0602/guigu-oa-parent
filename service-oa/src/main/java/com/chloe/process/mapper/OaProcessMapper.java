package com.chloe.process.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chloe.model.process.Process;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chloe.vo.process.ProcessQueryVo;
import com.chloe.vo.process.ProcessVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 审批类型 Mapper 接口
 * </p>
 *
 * @author chloe
 * @since 2024-07-24
 */
@Mapper
public interface OaProcessMapper extends BaseMapper<Process> {

    IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam,@Param("vo") ProcessQueryVo processQueryVo);
}
