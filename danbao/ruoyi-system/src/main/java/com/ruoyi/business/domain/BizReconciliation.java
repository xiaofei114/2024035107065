package com.ruoyi.business.domain;

import java.util.Date;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;

/**
 * 对账关联表实体类
 * 
 * @author ruoyi
 */
public class BizReconciliation
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 担保业务ID */
    @Excel(name = "担保业务ID")
    private Long guaranteeBusinessId;

    /** 银行流水ID */
    @Excel(name = "银行流水ID")
    private Long bankTransactionId;

    /** 对账类型 */
    @Excel(name = "对账类型", readConverterExp = "自动对账=自动对账,手动对账=手动对账")
    private String reconciliationType;

    /** 对账状态 */
    @Excel(name = "对账状态", readConverterExp = "未对账=未对账,自动匹配=自动匹配,待人工确认=待人工确认,人工确认=人工确认")
    private String reconciliationStatus;

    /** 操作人 */
    @Excel(name = "操作人")
    private String operator;

    /** 操作时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "操作时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date operateTime;

    // 关联对象，用于前端展示
    private BizGuaranteeFee guaranteeFee;
    private BizBankFlow bankFlow;
    
    // 查询参数
    private Map<String, Object> params;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGuaranteeBusinessId() {
        return guaranteeBusinessId;
    }

    public void setGuaranteeBusinessId(Long guaranteeBusinessId) {
        this.guaranteeBusinessId = guaranteeBusinessId;
    }

    public Long getBankTransactionId() {
        return bankTransactionId;
    }

    public void setBankTransactionId(Long bankTransactionId) {
        this.bankTransactionId = bankTransactionId;
    }

    public String getReconciliationType() {
        return reconciliationType;
    }

    public void setReconciliationType(String reconciliationType) {
        this.reconciliationType = reconciliationType;
    }

    public String getReconciliationStatus() {
        return reconciliationStatus;
    }

    public void setReconciliationStatus(String reconciliationStatus) {
        this.reconciliationStatus = reconciliationStatus;
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

    public BizGuaranteeFee getGuaranteeFee() {
        return guaranteeFee;
    }

    public void setGuaranteeFee(BizGuaranteeFee guaranteeFee) {
        this.guaranteeFee = guaranteeFee;
    }

    public BizBankFlow getBankFlow() {
        return bankFlow;
    }

    public void setBankFlow(BizBankFlow bankFlow) {
        this.bankFlow = bankFlow;
    }
    
    public Map<String, Object> getParams() {
        return params;
    }
    
    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
