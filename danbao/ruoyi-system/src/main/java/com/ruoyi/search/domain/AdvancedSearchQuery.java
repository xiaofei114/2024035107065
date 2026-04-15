package com.ruoyi.search.domain;

import java.io.Serializable;

/**
 * 高级检索查询参数
 * 支持多维度组合查询：标题、摘要模糊匹配，分类维度过滤，申请日期区间限定
 *
 * @author ruoyi
 */
public class AdvancedSearchQuery implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 标题关键词（模糊匹配） */
    private String titleKeyword;

    /** 摘要关键词（模糊匹配） */
    private String absKeyword;

    /** 分类号（精确匹配或模糊匹配） */
    private String classification;

    /** 申请日期开始时间（格式：yyyy-MM-dd） */
    private String startDate;

    /** 申请日期结束时间（格式：yyyy-MM-dd） */
    private String endDate;

    /** 页码（默认1） */
    private Integer pageNum = 1;

    /** 每页条数（默认10） */
    private Integer pageSize = 10;

    public String getTitleKeyword()
    {
        return titleKeyword;
    }

    public void setTitleKeyword(String titleKeyword)
    {
        this.titleKeyword = titleKeyword;
    }

    public String getAbsKeyword()
    {
        return absKeyword;
    }

    public void setAbsKeyword(String absKeyword)
    {
        this.absKeyword = absKeyword;
    }

    public String getClassification()
    {
        return classification;
    }

    public void setClassification(String classification)
    {
        this.classification = classification;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public Integer getPageNum()
    {
        return pageNum;
    }

    public void setPageNum(Integer pageNum)
    {
        this.pageNum = pageNum;
    }

    public Integer getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(Integer pageSize)
    {
        this.pageSize = pageSize;
    }

    /**
     * 判断是否有查询条件
     */
    public boolean hasQueryCondition()
    {
        return (titleKeyword != null && !titleKeyword.trim().isEmpty())
                || (absKeyword != null && !absKeyword.trim().isEmpty())
                || (classification != null && !classification.trim().isEmpty())
                || (startDate != null && !startDate.trim().isEmpty())
                || (endDate != null && !endDate.trim().isEmpty());
    }

    @Override
    public String toString()
    {
        return "AdvancedSearchQuery{" +
                "titleKeyword='" + titleKeyword + '\'' +
                ", absKeyword='" + absKeyword + '\'' +
                ", classification='" + classification + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                '}';
    }
}
