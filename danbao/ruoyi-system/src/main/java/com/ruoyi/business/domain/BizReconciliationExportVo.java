package com.ruoyi.business.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;

/**
 * 对账列表导出（与页面表格列一致，含关联的担保业务、银行流水信息）
 */
public class BizReconciliationExportVo
{
    @Excel(name = "对账状态")
    private String reconciliationStatus;

    @Excel(name = "产品名称")
    private String productName;

    @Excel(name = "债务人")
    private String debtorName;

    @Excel(name = "银行(担保)")
    private String guaranteeBankName;

    @Excel(name = "担保费", cellType = ColumnType.NUMERIC)
    private BigDecimal guaranteeFee;

    @Excel(name = "对方户名")
    private String oppositeName;

    @Excel(name = "交易金额", cellType = ColumnType.NUMERIC)
    private BigDecimal tradeAmount;

    @Excel(name = "对账类型")
    private String reconciliationType;

    @Excel(name = "操作人")
    private String operator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "操作时间", width = 20, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date operateTime;

    @Excel(name = "担保业务ID", cellType = ColumnType.NUMERIC)
    private Long guaranteeBusinessId;

    /** 无流水或占位 0 时导出为「-」 */
    @Excel(name = "银行流水ID")
    private String bankTransactionIdText;

    public String getReconciliationStatus()
    {
        return reconciliationStatus;
    }

    public void setReconciliationStatus(String reconciliationStatus)
    {
        this.reconciliationStatus = reconciliationStatus;
    }

    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    public String getDebtorName()
    {
        return debtorName;
    }

    public void setDebtorName(String debtorName)
    {
        this.debtorName = debtorName;
    }

    public String getGuaranteeBankName()
    {
        return guaranteeBankName;
    }

    public void setGuaranteeBankName(String guaranteeBankName)
    {
        this.guaranteeBankName = guaranteeBankName;
    }

    public BigDecimal getGuaranteeFee()
    {
        return guaranteeFee;
    }

    public void setGuaranteeFee(BigDecimal guaranteeFee)
    {
        this.guaranteeFee = guaranteeFee;
    }

    public String getOppositeName()
    {
        return oppositeName;
    }

    public void setOppositeName(String oppositeName)
    {
        this.oppositeName = oppositeName;
    }

    public BigDecimal getTradeAmount()
    {
        return tradeAmount;
    }

    public void setTradeAmount(BigDecimal tradeAmount)
    {
        this.tradeAmount = tradeAmount;
    }

    public String getReconciliationType()
    {
        return reconciliationType;
    }

    public void setReconciliationType(String reconciliationType)
    {
        this.reconciliationType = reconciliationType;
    }

    public String getOperator()
    {
        return operator;
    }

    public void setOperator(String operator)
    {
        this.operator = operator;
    }

    public Date getOperateTime()
    {
        return operateTime;
    }

    public void setOperateTime(Date operateTime)
    {
        this.operateTime = operateTime;
    }

    public Long getGuaranteeBusinessId()
    {
        return guaranteeBusinessId;
    }

    public void setGuaranteeBusinessId(Long guaranteeBusinessId)
    {
        this.guaranteeBusinessId = guaranteeBusinessId;
    }

    public String getBankTransactionIdText()
    {
        return bankTransactionIdText;
    }

    public void setBankTransactionIdText(String bankTransactionIdText)
    {
        this.bankTransactionIdText = bankTransactionIdText;
    }

    /**
     * 由带关联数据的 {@link BizReconciliation} 转为导出行（与前端表格展示一致）
     */
    public static BizReconciliationExportVo from(BizReconciliation r)
    {
        BizReconciliationExportVo vo = new BizReconciliationExportVo();
        vo.setReconciliationStatus(emptyToDash(r.getReconciliationStatus()));

        BizGuaranteeFee g = r.getGuaranteeFee();
        if (g != null)
        {
            vo.setProductName(emptyToDash(g.getProductName()));
            vo.setDebtorName(emptyToDash(g.getDebtorName()));
            vo.setGuaranteeBankName(emptyToDash(g.getBankName()));
            vo.setGuaranteeFee(g.getGuaranteeFee());
        }
        else
        {
            vo.setProductName("-");
            vo.setDebtorName("-");
            vo.setGuaranteeBankName("-");
            vo.setGuaranteeFee(null);
        }

        BizBankFlow b = r.getBankFlow();
        if (b != null)
        {
            vo.setOppositeName(emptyToDash(b.getOppositeName()));
            vo.setTradeAmount(b.getTradeAmount());
        }
        else
        {
            vo.setOppositeName("-");
            vo.setTradeAmount(null);
        }

        vo.setReconciliationType(emptyToDash(r.getReconciliationType()));
        vo.setOperator(emptyToDash(r.getOperator()));
        vo.setOperateTime(r.getOperateTime());
        vo.setGuaranteeBusinessId(r.getGuaranteeBusinessId());

        Long bid = r.getBankTransactionId();
        if (bid == null || bid == 0L)
        {
            vo.setBankTransactionIdText("-");
        }
        else
        {
            vo.setBankTransactionIdText(String.valueOf(bid));
        }
        return vo;
    }

    private static String emptyToDash(String s)
    {
        if (s == null || s.trim().isEmpty())
        {
            return "-";
        }
        return s;
    }
}
