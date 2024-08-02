package com.chloe.process.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chloe.model.process.ProcessTemplate;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 审批模板 服务类
 * </p>
 *
 * @author chloe
 * @since 2024-07-19
 */
public interface OaProcessTemplateService extends IService<ProcessTemplate> {

    IPage<ProcessTemplate> selectPage(Page<ProcessTemplate> pageParam);

    void publish(Long id);
}
