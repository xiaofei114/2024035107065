package com.ruoyi.search.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Solr 搜索结果封装类
 * 用于返回分页数据
 *
 * @author ruoyi
 */
public class SolrSearchResult implements Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 搜索结果列表
     */
    private List<SearchItem> records;

    /**
     * 当前页码
     */
    private int pageNum;

    /**
     * 每页条数
     */
    private int pageSize;

    public SolrSearchResult()
    {
    }

    public SolrSearchResult(long total, List<SearchItem> records, int pageNum, int pageSize)
    {
        this.total = total;
        this.records = records;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    public long getTotal()
    {
        return total;
    }

    public void setTotal(long total)
    {
        this.total = total;
    }

    public List<SearchItem> getRecords()
    {
        return records;
    }

    public void setRecords(List<SearchItem> records)
    {
        this.records = records;
    }

    public int getPageNum()
    {
        return pageNum;
    }

    public void setPageNum(int pageNum)
    {
        this.pageNum = pageNum;
    }

    public int getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(int pageSize)
    {
        this.pageSize = pageSize;
    }
}
