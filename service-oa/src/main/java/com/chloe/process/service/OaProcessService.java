package com.chloe.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chloe.model.process.Process;
import com.baomidou.mybatisplus.extension.service.IService;
import com.chloe.vo.process.ApprovalVo;
import com.chloe.vo.process.ProcessFormVo;
import com.chloe.vo.process.ProcessQueryVo;
import com.chloe.vo.process.ProcessVo;
import me.chanjar.weixin.common.error.WxErrorException;

import java.util.Map;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author chloe
 * @since 2024-07-24
 */
public interface OaProcessService extends IService<Process> {

    IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo);
    void deployByZip(String deployPath);

    void startUp(ProcessFormVo processFormVo) throws WxErrorException;

    IPage<ProcessVo> findPending(Page<java.lang.Process> pageParam);

    Map<String, Object> show(Long id);

    void approve(ApprovalVo approvalVo) throws WxErrorException;

    IPage<ProcessVo> findProcessed(Page<java.lang.Process> pageParam);

    IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam);
}
