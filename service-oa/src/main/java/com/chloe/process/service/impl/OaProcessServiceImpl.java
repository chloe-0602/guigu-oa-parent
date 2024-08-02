package com.chloe.process.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chloe.auth.service.SysMenuService;
import com.chloe.auth.service.SysUserService;
import com.chloe.model.process.Process;
import com.chloe.model.process.ProcessRecord;
import com.chloe.model.process.ProcessTemplate;
import com.chloe.model.system.SysUser;
import com.chloe.process.mapper.OaProcessMapper;
import com.chloe.process.mapper.OaProcessRecordMapper;
import com.chloe.process.service.OaProcessRecordService;
import com.chloe.process.service.OaProcessService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chloe.process.service.OaProcessTemplateService;
import com.chloe.security.custom.LoginUserInfoHelper;
import com.chloe.vo.process.ApprovalVo;
import com.chloe.vo.process.ProcessFormVo;
import com.chloe.vo.process.ProcessQueryVo;
import com.chloe.vo.process.ProcessVo;
import com.chloe.wechat.service.MessageService;
import me.chanjar.weixin.common.error.WxErrorException;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author chloe
 * @since 2024-07-24
 */
@Service
public class OaProcessServiceImpl extends ServiceImpl<OaProcessMapper, Process> implements OaProcessService {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    SysUserService sysUserService;
    @Autowired
    OaProcessTemplateService processTemplateService;
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    TaskService taskService;
    @Autowired
    OaProcessRecordService processRecordService;

    @Autowired
    HistoryService historyService;
    @Autowired
    private MessageService messageService;

    @Override
    public IPage<ProcessVo> selectPage(Page<ProcessVo> pageParam, ProcessQueryVo processQueryVo) {
        return baseMapper.selectPage(pageParam, processQueryVo);
    }

    @Override
    public void deployByZip(String deployPath) {
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream(deployPath);

        ZipInputStream zipInputStream = new ZipInputStream(inputStream);

        Deployment deployment = repositoryService.createDeployment()
                .addZipInputStream(zipInputStream)
                .deploy();
    }

    @Override
    public void startUp(ProcessFormVo processFormVo) throws WxErrorException {
        SysUser currentSysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());
        ProcessTemplate currentProcessTemplate = processTemplateService.getById(processFormVo.getProcessTemplateId());

        // 启动实例、创建一个oa_process记录，将实例的id绑定到这个oa_process记录上
        Process process = new Process();
        BeanUtils.copyProperties(processFormVo, process);
        process.setProcessCode(System.currentTimeMillis() + "");
        process.setUserId(LoginUserInfoHelper.getUserId());
        process.setTitle(currentSysUser.getUsername() + "发起" + currentProcessTemplate.getName() + "申请");
        process.setStatus(1);
        baseMapper.insert(process);

        Map<String, Object> variables = new HashMap<>();
        JSONObject jsonObject = JSON.parseObject(processFormVo.getFormValues());
        JSONObject formData = jsonObject.getJSONObject("formData");
        for (Map.Entry<String, Object> entry : formData.entrySet()) {
            variables.put(entry.getKey(), entry.getValue());
        }
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(
                currentProcessTemplate.getProcessDefinitionKey(),
                String.valueOf(process.getId()),
                variables);

        process.setProcessInstanceId(processInstance.getId());

        //计算下一个审批人，可能有多个（并行审批)
        List<Task> taskList = this.getCurrentTaskList(processInstance.getId());
        if(!CollectionUtils.isEmpty(taskList)){
            List<String> assigneeList = new ArrayList<>();
            for (Task task : taskList) {
                SysUser user = sysUserService.getByUserName(task.getAssignee());
                assigneeList.add(user.getUsername());
                //推送消息给下一个审批人，后续完善
                messageService.pushPendingMessage(process.getId(), user.getId(), task.getId());

            }
            process.setDescription("等待" + String.join( ",", assigneeList) + "审批");
        }

        processRecordService.record(process.getId(), 1, "发起申请");

        baseMapper.updateById(process);
    }

    @Override
    public IPage<ProcessVo> findPending(Page<java.lang.Process> pageParam) {
        // 当前用户 -》 当前用户的task -》 对应的processId(其实就是task中的key) -> process -> ProcessVo
        List<ProcessVo> processVoList = new ArrayList<>();

        TaskQuery query = taskService.createTaskQuery()
                .taskAssignee(LoginUserInfoHelper.getUsername())
                .orderByTaskCreateTime()
                .desc();

        long totalCount = query.count();

        int currenPage = (int)((pageParam.getCurrent() - 1) * pageParam.getSize());
        int size = (int) pageParam.getSize();
        List<Task> taskList = query.listPage(currenPage, size);

        for (Task task : taskList) {
            String processInstanceId = task.getProcessInstanceId();
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult();
            if(processInstance == null){
                continue;
            }
            String businessKey = processInstance.getBusinessKey();
            if (businessKey == null){
                continue;
            }
            long processId = Long.parseLong(businessKey);
            Process process = baseMapper.selectById(processId);

            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process, processVo);
            processVo.setTaskId(task.getId());
            processVoList.add(processVo);
        }


        IPage<ProcessVo> processVoPage = new Page<>(pageParam.getCurrent(),
                pageParam.getSize(),
                totalCount
        );
        processVoPage.setRecords(processVoList);
        return processVoPage;
    }

    // 获取审批详情
    @Override
    public Map<String, Object> show(Long id) {

        //1. 获取Process
        Process process = this.getById(id);

        //2. 获取Process Record
        LambdaQueryWrapper<ProcessRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProcessRecord::getProcessId, id);
        List<ProcessRecord> processRecordList = processRecordService.list(wrapper);

        //3. 获取Process template
        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());

        //4. 判断当前用户是否可以审批
        //可以看到不一定可以审批； 不能重复审批
        boolean isApprove = false;
        List<Task> currentTaskList = this.getCurrentTaskList(process.getProcessInstanceId());
        for (Task task : currentTaskList) {
            if(task.getAssignee().equals(LoginUserInfoHelper.getUsername())){
                isApprove = true;
            }
        }
        //5. 组装数据到map中
        Map<String, Object> map = new HashMap<>();
        map.put("process", process);
        map.put("processRecordList", processRecordList);
        map.put("processTemplate", processTemplate);
        map.put("isApprove", isApprove);

        return map;
    }

    @Override
    public void approve(ApprovalVo approvalVo) throws WxErrorException {
        // 1. 获取流程变量
        String taskId = approvalVo.getTaskId();
        Map<String, Object> variables = taskService.getVariables(taskId);
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }

        // 2. 判断审批状态值
        if(approvalVo.getStatus() == 1){
            // 2.1 1 审批通过
            Map<String, Object> variables1 = new HashMap<String, Object>();
            taskService.complete(taskId, variables1);
        }else {
            // 2.2 -1 审批驳回，流程结束
            this.endTask(taskId);
        }

        // 3. 记录审批记录
        String description = approvalVo.getStatus() == 1 ? "审批通过" : "审批驳回";
        processRecordService.record(approvalVo.getProcessId(), approvalVo.getStatus(), description);

        // 计算下一个审批人
        Process process = this.getById(approvalVo.getProcessId());
        List<Task> currentTaskList = this.getCurrentTaskList(process.getProcessInstanceId());
        if (!CollectionUtils.isEmpty(currentTaskList)){
            List<String> assigneeList = new ArrayList<>();
            for (Task task : currentTaskList) {
                SysUser user = sysUserService.getByUserName(task.getAssignee());
                assigneeList.add(user.getUsername());

                // 推送消息给下一个审批人，后续完善 ToDO
                messageService.pushPendingMessage(process.getId(), user.getId(), task.getId());
            }
        }else{
            if(approvalVo.getStatus().intValue() == 1) {
                process.setDescription("审批完成（同意）");
                process.setStatus(2);
            } else {
                process.setDescription("审批完成（拒绝）");
                process.setStatus(-1);
            }
        }

        this.updateById(process);
    }

    @Override
    public IPage<ProcessVo> findProcessed(Page<java.lang.Process> pageParam) {
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(LoginUserInfoHelper.getUsername())
                .finished()
                .orderByTaskCreateTime()
                .desc();

        List<HistoricTaskInstance> list = query.listPage((int) ((pageParam.getCurrent() - 1) * pageParam.getSize()), (int) pageParam.getSize());
        long totalCount = query.count();

        List<ProcessVo> processList = new ArrayList<>();
        for (HistoricTaskInstance item : list) {
            String processInstanceId = item.getProcessInstanceId();
            Process process = this.getOne(new LambdaQueryWrapper<Process>()
                    .eq(Process::getProcessInstanceId, processInstanceId));
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process, processVo);
            processVo.setTaskId("0");
            processList.add(processVo);
        }

        Page<ProcessVo> processVoPage = new Page<ProcessVo>(pageParam.getCurrent(), pageParam.getSize(), totalCount).setRecords(processList);
        processVoPage.setRecords(processList);

        return processVoPage;
    }

    @Override
    public IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam) {
        ProcessQueryVo processQueryVo = new ProcessQueryVo();
        processQueryVo.setUserId(LoginUserInfoHelper.getUserId());
        IPage<ProcessVo> processVoIPage = this.baseMapper.selectPage(pageParam, processQueryVo);
        for (ProcessVo itemVO : processVoIPage.getRecords()) {
            itemVO.setTaskId("0");
        }
        return processVoIPage;
    }

    private void endTask(String taskId) {
        //  当前任务
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        List endEventList = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);
        // 并行任务可能为null
        if(CollectionUtils.isEmpty(endEventList)) {
            return;
        }
        FlowNode endFlowNode = (FlowNode) endEventList.get(0);
        FlowNode currentFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey());

        //  临时保存当前活动的原始方向
        List originalSequenceFlowList = new ArrayList<>();
        originalSequenceFlowList.addAll(currentFlowNode.getOutgoingFlows());
        //  清理活动方向
        currentFlowNode.getOutgoingFlows().clear();

        //  建立新方向
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlowId");
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(endFlowNode);
        List newSequenceFlowList = new ArrayList<>();
        newSequenceFlowList.add(newSequenceFlow);
        //  当前节点指向新的方向
        currentFlowNode.setOutgoingFlows(newSequenceFlowList);

        //  完成当前任务
        taskService.complete(task.getId());
    }

    private List<Task> getCurrentTaskList(String processInstanceId) {
        return taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list();
    }
}
