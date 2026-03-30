package com.ruoyi.business.domain;

import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.annotation.Excel.ColumnType;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 账单台账对象 biz_bank_flow
 * 
 * @author ruoyi
 */
public class BizBankFlow extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Excel(name = "序号", cellType = ColumnType.NUMERIC)
    private Long id;

    /** 对方户名 */
    @Excel(name = "对方户名")
    private String oppositeName;

    /** 交易金额 */
    @Excel(name = "交易金额", cellType = ColumnType.NUMERIC)
    private BigDecimal tradeAmount;

    /** 交易时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "交易时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date tradeTime;

    /** 所属银行 */
    @Excel(name = "所属银行")
    private String bankName;

    /** 支付类型（0=支出，1=收入） */
    @Excel(name = "支付类型", readConverterExp = "0=支出,1=收入")
    private Integer payType;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    @NotBlank(message = "对方户名不能为空")
    public String getOppositeName()
    {
        return oppositeName;
    }

    public void setOppositeName(String oppositeName)
    {
        this.oppositeName = oppositeName;
    }

    @NotNull(message = "交易金额不能为空")
    public BigDecimal getTradeAmount()
    {
        return tradeAmount;
    }

    public void setTradeAmount(BigDecimal tradeAmount)
    {
        this.tradeAmount = tradeAmount;
    }

    @NotNull(message = "交易时间不能为空")
    public Date getTradeTime()
    {
        return tradeTime;
    }

    public void setTradeTime(Date tradeTime)
    {
        this.tradeTime = tradeTime;
    }

    @NotBlank(message = "所属银行不能为空")
    public String getBankName()
    {
        return bankName;
    }

    public void setBankName(String bankName)
    {
        this.bankName = bankName;
    }

    @NotNull(message = "支付类型不能为空")
    public Integer getPayType()
    {
        return payType;
    }

    public void setPayType(Integer payType)
    {
        this.payType = payType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("oppositeName", getOppositeName())
            .append("tradeAmount", getTradeAmount())
            .append("tradeTime", getTradeTime())
            .append("bankName", getBankName())
            .append("payType", getPayType())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("createBy", getCreateBy())
            .append("updateBy", getUpdateBy())
            .append("remark", getRemark())
            .toString();
    }
}
