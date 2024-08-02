package com.chloe.process.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chloe.model.process.ProcessTemplate;
import com.chloe.model.process.ProcessType;
import com.chloe.process.mapper.OaProcessTypeMapper;
import com.chloe.process.service.OaProcessTemplateService;
import com.chloe.process.service.OaProcessTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author chloe
 * @since 2024-07-19
 */
@Service
public class OaProcessTypeServiceImpl extends ServiceImpl<OaProcessTypeMapper, ProcessType> implements OaProcessTypeService {

    @Autowired
    OaProcessTemplateService processTemplateService;

    @Override
    public List<ProcessType> findProcessType() {
        List<ProcessType> processTypeList = baseMapper.selectList(null);
        processTypeList.forEach(processType -> {
            Long typeId = processType.getId();
            LambdaQueryWrapper<ProcessTemplate> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProcessTemplate::getProcessTypeId,typeId);
            List<ProcessTemplate> processTemplateList = processTemplateService.list(wrapper);
            processType.setProcessTemplateList(processTemplateList);
        });
        return processTypeList;
    }
}
