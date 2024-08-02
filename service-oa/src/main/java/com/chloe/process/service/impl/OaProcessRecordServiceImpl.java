package com.chloe.process.service.impl;

import com.chloe.auth.service.SysUserService;
import com.chloe.model.process.ProcessRecord;
import com.chloe.model.system.SysUser;
import com.chloe.process.mapper.OaProcessRecordMapper;
import com.chloe.process.service.OaProcessRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chloe.security.custom.LoginUserInfoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 审批记录 服务实现类
 * </p>
 *
 * @author chloe
 * @since 2024-07-25
 */
@Service
public class OaProcessRecordServiceImpl extends ServiceImpl<OaProcessRecordMapper, ProcessRecord> implements OaProcessRecordService {

    @Autowired
    private SysUserService sysUserService;

    @Override
    public void record(Long processId, Integer status, String description) {
        SysUser sysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());

        ProcessRecord processRecord = new ProcessRecord();
        processRecord.setProcessId(processId);
        processRecord.setStatus(status);
        processRecord.setDescription(description);
        processRecord.setOperateUserId(sysUser.getId());
        processRecord.setOperateUser(sysUser.getName());

        baseMapper.insert(processRecord);
    }
}
