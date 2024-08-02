package com.chloe.wechat.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chloe.auth.service.SysUserService;
import com.chloe.model.process.Process;
import com.chloe.model.process.ProcessTemplate;
import com.chloe.model.system.SysUser;
import com.chloe.process.service.OaProcessService;
import com.chloe.process.service.OaProcessTemplateService;
import com.chloe.security.custom.LoginUserInfoHelper;
import com.chloe.wechat.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Map;
@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Resource
    private WxMpService wxMpService;

    @Resource
    private OaProcessService processService;

    @Resource
    private OaProcessTemplateService processTemplateService;

    @Resource
    private SysUserService sysUserService;
    @Override
    public void pushPendingMessage(Long processId, Long userId, String taskId) throws WxErrorException {
        Process process = processService.getById(processId);
        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());
        SysUser sysUser = sysUserService.getById(userId);
        SysUser submitSysUser = sysUserService.getById(process.getUserId());
        String openid = sysUser.getOpenId();
        //方便测试，给默认值（开发者本人的openId）
        if(StringUtils.isEmpty(openid)) {
            openid = "omwf25izKON9dktgoy0dogqvnGhk";
        }
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(openid)//要推送的用户openid
                .templateId("2QoelRdVg_bCsQSNXDKFm7qXENvcV0Qns43GKPUHf30")//模板id
                .url("http://chloe2.v7.idcfengye.com/#/show/"+processId+"/"+taskId)//点击模板消息要访问的网址
                .build();
        JSONObject jsonObject = JSON.parseObject(process.getFormValues());
        JSONObject formShowData = jsonObject.getJSONObject("formShowData");
        StringBuffer content = new StringBuffer();
        for (Map.Entry entry : formShowData.entrySet()) {
            content.append(entry.getKey()).append("：").append(entry.getValue()).append("\n ");
        }
        templateMessage.addData(new WxMpTemplateData("first", submitSysUser.getName()+"提交了"+processTemplate.getName()+"审批申请，请注意查看。", "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword1", process.getProcessCode(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword2", new DateTime(process.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"), "#272727"));
        templateMessage.addData(new WxMpTemplateData("content", content.toString(), "#272727"));
        String msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        log.info("推送消息返回：{}", msg);
    }

    @Override
    public void pushProcessedMessage(Long processId, Long userId, Integer status) throws WxErrorException {
        Process process = processService.getById(processId);
        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());
        SysUser sysUser = sysUserService.getById(userId);
        SysUser currentSysUser = sysUserService.getById(LoginUserInfoHelper.getUserId());
        String openid = sysUser.getOpenId();
        if(StringUtils.isEmpty(openid)) {
            openid = "omwf25izKON9dktgoy0dogqvnGhk";
        }
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                .toUser(openid)//要推送的用户openid
                .templateId("zdW3F6JzTWQva_Y1_XnCxrYuenF3p1uz4Darbl62NSM")//模板id
                .url("http://chloe2.v7.idcfengye.com/#/show/"+processId+"/0")//点击模板消息要访问的网址
                .build();
        JSONObject jsonObject = JSON.parseObject(process.getFormValues());
        JSONObject formShowData = jsonObject.getJSONObject("formShowData");
        StringBuffer content = new StringBuffer();
        for (Map.Entry entry : formShowData.entrySet()) {
            content.append(entry.getKey()).append("：").append(entry.getValue()).append("\n ");
        }
        templateMessage.addData(new WxMpTemplateData("first", "你发起的"+processTemplate.getName()+"审批申请已经被处理了，请注意查看。", "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword1", process.getProcessCode(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword2", new DateTime(process.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword3", currentSysUser.getName(), "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword4", status == 1 ? "审批通过" : "审批拒绝", status == 1 ? "#009966" : "#FF0033"));
        templateMessage.addData(new WxMpTemplateData("content", content.toString(), "#272727"));
        String msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
        log.info("推送消息返回：{}", msg);
    }
}
