package com.chloe.process.service;

import com.chloe.model.process.ProcessType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author chloe
 * @since 2024-07-19
 */
public interface OaProcessTypeService extends IService<ProcessType> {

    List<ProcessType> findProcessType();
}
