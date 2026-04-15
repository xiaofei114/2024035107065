package com.ruoyi.search.service;

import com.ruoyi.search.domain.AdvancedSearchQuery;
import com.ruoyi.search.domain.FacetStatResult;
import com.ruoyi.search.domain.SearchItem;
import com.ruoyi.search.domain.SolrSearchResult;

import java.util.List;

/**
 * Solr 搜索服务接口
 *
 * @author ruoyi
 */
public interface ISearchService
{
    /**
     * 根据关键词搜索文档（不分页）
     *
     * @param keyword 搜索关键词
     * @return 匹配的文档列表
     */
    List<SearchItem> searchByKeyword(String keyword);

    /**
     * 根据关键词搜索文档（分页）
     *
     * @param keyword 搜索关键词
     * @param pageNum  页码（从1开始）
     * @param pageSize 每页条数
     * @return 分页搜索结果
     */
    SolrSearchResult searchByKeyword(String keyword, int pageNum, int pageSize);

    /**
     * 高级检索：多维度组合查询
     * 支持按标题、摘要模糊匹配，分类维度过滤，申请日期区间限定组合查询条件
     *
     * @param query 查询参数对象
     * @return 分页搜索结果
     */
    SolrSearchResult advancedSearch(AdvancedSearchQuery query);

    /**
     * 按省份统计专利数量（用于柱状图）
     *
     * @param keyword 搜索关键词（可选，为空则统计全部）
     * @param limit   返回前N个省份
     * @return 省份统计结果
     */
    FacetStatResult statByProvince(String keyword, int limit);

    /**
     * 按类型统计专利数量（用于饼图）
     *
     * @param keyword 搜索关键词（可选，为空则统计全部）
     * @return 类型统计结果
     */
    FacetStatResult statByType(String keyword);

    /**
     * 按申请年份统计专利数量（用于时间趋势图）
     *
     * @param keyword 搜索关键词（可选，为空则统计全部）
     * @param startYear 起始年份
     * @param endYear   结束年份
     * @return 年份统计结果
     */
    FacetStatResult statByYear(String keyword, int startYear, int endYear);
}
