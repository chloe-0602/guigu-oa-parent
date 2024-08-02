package com.chloe.wechat.service;

import me.chanjar.weixin.common.error.WxErrorException;

public interface MessageService {
    /**
     * 推送待审批人员
     * @param processId
     * @param userId
     * @param taskId
     */
    void pushPendingMessage(Long processId, Long userId, String taskId) throws WxErrorException;

    /**
     * 审批后推送提交审批人员
     * @param processId
     * @param userId
     * @param status
     */
    void pushProcessedMessage(Long processId, Long userId, Integer status) throws WxErrorException;
        }
