package com.chloe.process.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chloe.model.process.ProcessTemplate;
import com.chloe.process.mapper.OaProcessTemplateMapper;
import com.chloe.process.service.OaProcessService;
import com.chloe.process.service.OaProcessTemplateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 审批模板 服务实现类
 * </p>
 *
 * @author chloe
 * @since 2024-07-19
 */
@Service
public class OaProcessTemplateServiceImpl extends ServiceImpl<OaProcessTemplateMapper, ProcessTemplate> implements OaProcessTemplateService {


    @Autowired
    private OaProcessService processService;

    @Override
    public IPage<ProcessTemplate> selectPage(Page<ProcessTemplate> pageParam) {

        Page<ProcessTemplate> processTemplatePage = this.baseMapper.selectPage(pageParam, null);
        processTemplatePage.getRecords().forEach(processTemplate -> {
            processTemplate.setProcessTypeName(this.baseMapper.selectById(processTemplate.getId()).getName());
        });

        return processTemplatePage;
    }

    @Override
    public void publish(Long id) {
        ProcessTemplate processTemplate = this.getById(id);
        processTemplate.setStatus(1);
        this.baseMapper.updateById(processTemplate);

        //优先发布在线流程设计
        if(!StringUtils.isEmpty(processTemplate.getProcessDefinitionPath())){
            processService.deployByZip(processTemplate.getProcessDefinitionPath());
        }
    }
}
