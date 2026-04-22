package com.ruoyi.search.domain;

import org.apache.solr.client.solrj.beans.Field;

import java.io.Serializable;

/**
 * Solr 搜索实体类
 * 对应 cnpatent core 的专利文档结构
 *
 * @author ruoyi
 */
public class SearchItem implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 文档唯一标识 */
    @Field("id")
    private String id;

    /** 申请号 */
    @Field("application_no")
    private String applicationNo;

    /** 公开号 */
    @Field("publication_no")
    private String publicationNo;

    /** 申请日期 */
    @Field("application_date")
    private String applicationDate;

    /** 专利标题 */
    @Field("title")
    private String title;

    /** 摘要 */
    @Field("abs")
    private String abs;

    /** 申请人 */
    @Field("applicant")
    private String applicant;

    /** 发明人 */
    @Field("inventor")
    private String inventor;

    /** 专利代理机构 */
    @Field("patent_agency")
    private String patentAgency;

    /** 分类号 */
    @Field("classification")
    private String classification;

    /** 主分类号 */
    @Field("main_classification")
    private String mainClassification;

    /** 地址 */
    @Field("address")
    private String address;

    /** 专利类型 */
    @Field("type_name")
    private String typeName;

    /** 法律状态 */
    @Field("legaltype")
    private String legalType;

    /** 来源 */
    @Field("source")
    private String source;

    /** PDF文件路径 */
    @Field("pdffile")
    private String pdfFile;

    /** 权力要求书 */
    @Field("pat_claim")
    private String patClaim;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getApplicationNo()
    {
        return applicationNo;
    }

    public void setApplicationNo(String applicationNo)
    {
        this.applicationNo = applicationNo;
    }

    public String getPublicationNo()
    {
        return publicationNo;
    }

    public void setPublicationNo(String publicationNo)
    {
        this.publicationNo = publicationNo;
    }

    public String getApplicationDate()
    {
        return applicationDate;
    }

    public void setApplicationDate(String applicationDate)
    {
        this.applicationDate = applicationDate;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getAbs()
    {
        return abs;
    }

    public void setAbs(String abs)
    {
        this.abs = abs;
    }

    public String getApplicant()
    {
        return applicant;
    }

    public void setApplicant(String applicant)
    {
        this.applicant = applicant;
    }

    public String getInventor()
    {
        return inventor;
    }

    public void setInventor(String inventor)
    {
        this.inventor = inventor;
    }

    public String getPatentAgency()
    {
        return patentAgency;
    }

    public void setPatentAgency(String patentAgency)
    {
        this.patentAgency = patentAgency;
    }

    public String getClassification()
    {
        return classification;
    }

    public void setClassification(String classification)
    {
        this.classification = classification;
    }

    public String getMainClassification()
    {
        return mainClassification;
    }

    public void setMainClassification(String mainClassification)
    {
        this.mainClassification = mainClassification;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public void setTypeName(String typeName)
    {
        this.typeName = typeName;
    }

    public String getLegalType()
    {
        return legalType;
    }

    public void setLegalType(String legalType)
    {
        this.legalType = legalType;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String getPdfFile()
    {
        return pdfFile;
    }

    public void setPdfFile(String pdfFile)
    {
        this.pdfFile = pdfFile;
    }

    public String getPatClaim()
    {
        return patClaim;
    }

    public void setPatClaim(String patClaim)
    {
        this.patClaim = patClaim;
    }

    @Override
    public String toString()
    {
        return "SearchItem{" +
                "id='" + id + '\'' +
                ", applicationNo='" + applicationNo + '\'' +
                ", publicationNo='" + publicationNo + '\'' +
                ", applicationDate='" + applicationDate + '\'' +
                ", title='" + title + '\'' +
                ", abs='" + abs + '\'' +
                ", applicant='" + applicant + '\'' +
                ", inventor='" + inventor + '\'' +
                ", patentAgency='" + patentAgency + '\'' +
                ", classification='" + classification + '\'' +
                ", mainClassification='" + mainClassification + '\'' +
                ", address='" + address + '\'' +
                ", typeName='" + typeName + '\'' +
                ", legalType='" + legalType + '\'' +
                ", source='" + source + '\'' +
                ", pdfFile='" + pdfFile + '\'' +
                ", patClaim='" + patClaim + '\'' +
                '}';
    }
}
