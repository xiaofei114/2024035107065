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
 * 担保业务台账对象 biz_guarantee_fee
 * 
 * @author ruoyi
 */
public class BizGuaranteeFee extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @Excel(name = "序号", cellType = ColumnType.NUMERIC)
    private Long id;

    /** 产品名称 */
    @Excel(name = "产品名称")
    private String productName;

    /** 所属银行 */
    @Excel(name = "所属银行")
    private String bankName;

    /** 债务人名称 */
    @Excel(name = "债务人名称")
    private String debtorName;

    /** 债务人证件号 */
    @Excel(name = "债务人证件号")
    private String debtorIdCard;

    /** 主债权起始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "主债权起始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date creditStartDate;

    /** 主债权到期时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "主债权到期时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date creditEndDate;

    /** 主债权金额 */
    @Excel(name = "主债权金额", cellType = ColumnType.NUMERIC)
    private BigDecimal creditAmount;

    /** 担保费费率 */
    @Excel(name = "担保费费率", cellType = ColumnType.NUMERIC)
    private BigDecimal guaranteeFeeRate;

    /** 担保费金额 */
    @Excel(name = "担保费金额", cellType = ColumnType.NUMERIC)
    private BigDecimal guaranteeFee;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    @NotBlank(message = "产品名称不能为空")
    public String getProductName()
    {
        return productName;
    }

    public void setProductName(String productName)
    {
        this.productName = productName;
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

    @NotBlank(message = "债务人名称不能为空")
    public String getDebtorName()
    {
        return debtorName;
    }

    public void setDebtorName(String debtorName)
    {
        this.debtorName = debtorName;
    }

    public String getDebtorIdCard()
    {
        return debtorIdCard;
    }

    public void setDebtorIdCard(String debtorIdCard)
    {
        this.debtorIdCard = debtorIdCard;
    }

    @NotNull(message = "主债权起始时间不能为空")
    public Date getCreditStartDate()
    {
        return creditStartDate;
    }

    public void setCreditStartDate(Date creditStartDate)
    {
        this.creditStartDate = creditStartDate;
    }

    @NotNull(message = "主债权到期时间不能为空")
    public Date getCreditEndDate()
    {
        return creditEndDate;
    }

    public void setCreditEndDate(Date creditEndDate)
    {
        this.creditEndDate = creditEndDate;
    }

    @NotNull(message = "主债权金额不能为空")
    public BigDecimal getCreditAmount()
    {
        return creditAmount;
    }

    public void setCreditAmount(BigDecimal creditAmount)
    {
        this.creditAmount = creditAmount;
    }

    @NotNull(message = "担保费费率不能为空")
    public BigDecimal getGuaranteeFeeRate()
    {
        return guaranteeFeeRate;
    }

    public void setGuaranteeFeeRate(BigDecimal guaranteeFeeRate)
    {
        this.guaranteeFeeRate = guaranteeFeeRate;
    }

    public BigDecimal getGuaranteeFee()
    {
        return guaranteeFee;
    }

    public void setGuaranteeFee(BigDecimal guaranteeFee)
    {
        this.guaranteeFee = guaranteeFee;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("productName", getProductName())
            .append("bankName", getBankName())
            .append("debtorName", getDebtorName())
            .append("debtorIdCard", getDebtorIdCard())
            .append("creditStartDate", getCreditStartDate())
            .append("creditEndDate", getCreditEndDate())
            .append("creditAmount", getCreditAmount())
            .append("guaranteeFeeRate", getGuaranteeFeeRate())
            .append("guaranteeFee", getGuaranteeFee())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("createBy", getCreateBy())
            .append("updateBy", getUpdateBy())
            .append("remark", getRemark())
            .toString();
    }
}
