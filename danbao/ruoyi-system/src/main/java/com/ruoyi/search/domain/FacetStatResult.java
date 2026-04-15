package com.ruoyi.search.domain;

import java.util.List;
import java.util.Map;

/**
 * Solr Facet 统计结果封装类
 * 用于存储各维度聚合统计数据
 *
 * @author ruoyi
 */
public class FacetStatResult
{
    /**
     * 统计维度名称
     */
    private String dimension;

    /**
     * 统计项列表，每项包含名称和数量
     */
    private List<FacetItem> items;

    /**
     * 总计数量
     */
    private long total;

    public FacetStatResult()
    {
    }

    public FacetStatResult(String dimension, List<FacetItem> items, long total)
    {
        this.dimension = dimension;
        this.items = items;
        this.total = total;
    }

    public String getDimension()
    {
        return dimension;
    }

    public void setDimension(String dimension)
    {
        this.dimension = dimension;
    }

    public List<FacetItem> getItems()
    {
        return items;
    }

    public void setItems(List<FacetItem> items)
    {
        this.items = items;
    }

    public long getTotal()
    {
        return total;
    }

    public void setTotal(long total)
    {
        this.total = total;
    }

    /**
     * Facet 统计项内部类
     */
    public static class FacetItem
    {
        /**
         * 统计项名称（如省份名、类型名、年份）
         */
        private String name;

        /**
         * 该统计项对应的专利数量
         */
        private long count;

        /**
         * 占比百分比
         */
        private double percentage;

        public FacetItem()
        {
        }

        public FacetItem(String name, long count)
        {
            this.name = name;
            this.count = count;
        }

        public FacetItem(String name, long count, double percentage)
        {
            this.name = name;
            this.count = count;
            this.percentage = percentage;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public long getCount()
        {
            return count;
        }

        public void setCount(long count)
        {
            this.count = count;
        }

        public double getPercentage()
        {
            return percentage;
        }

        public void setPercentage(double percentage)
        {
            this.percentage = percentage;
        }
    }
}
