package com.ruoyi.business.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

/**
 * 对账日志表实体类
 * 
 * @author ruoyi
 */
public class BizReconciliationLog
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 对账关联ID */
    @Excel(name = "对账关联ID")
    private Long reconciliationId;

    /** 操作类型 */
    @Excel(name = "操作类型")
    private String operationType;

    /** 原对账状态 */
    @Excel(name = "原对账状态", readConverterExp = "未对账=未对账,自动匹配=自动匹配,待人工确认=待人工确认,人工确认=人工确认")
    private String originalStatus;

    /** 新对账状态 */
    @Excel(name = "新对账状态", readConverterExp = "未对账=未对账,自动匹配=自动匹配,待人工确认=待人工确认,人工确认=人工确认")
    private String newStatus;

    /** 操作人 */
    @Excel(name = "操作人")
    private String operator;

    /** 操作时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "操作时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date operateTime;

    /** 操作IP地址 */
    @Excel(name = "操作IP地址")
    private String operatorIp;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReconciliationId() {
        return reconciliationId;
    }

    public void setReconciliationId(Long reconciliationId) {
        this.reconciliationId = reconciliationId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOriginalStatus() {
        return originalStatus;
    }

    public void setOriginalStatus(String originalStatus) {
        this.originalStatus = originalStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getOperatorIp() {
        return operatorIp;
    }

    public void setOperatorIp(String operatorIp) {
        this.operatorIp = operatorIp;
    }
}
