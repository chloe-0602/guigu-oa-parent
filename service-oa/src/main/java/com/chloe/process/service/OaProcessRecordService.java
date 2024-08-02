package com.chloe.process.service;

import com.chloe.model.process.ProcessRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 审批记录 服务类
 * </p>
 *
 * @author chloe
 * @since 2024-07-25
 */
public interface OaProcessRecordService extends IService<ProcessRecord> {
    void record(Long processId, Integer status, String description);
}
