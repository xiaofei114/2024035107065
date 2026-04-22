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

    // ==================== 数据管理CRUD接口 ====================

    /**
     * 根据ID获取专利文档
     *
     * @param id 文档ID
     * @return 专利文档
     */
    SearchItem getById(String id);

    /**
     * 添加专利文档到Solr
     *
     * @param item 专利文档
     * @return 是否成功
     */
    boolean add(SearchItem item);

    /**
     * 更新Solr中的专利文档
     *
     * @param item 专利文档
     * @return 是否成功
     */
    boolean update(SearchItem item);

    /**
     * 删除Solr中的专利文档
     *
     * @param id 文档ID
     * @return 是否成功
     */
    boolean delete(String id);

    /**
     * 批量删除Solr中的专利文档
     *
     * @param ids 文档ID列表
     * @return 是否成功
     */
    boolean deleteBatch(List<String> ids);

    /**
     * 分页查询专利列表（数据管理用）
     *
     * @param keyword  标题关键词
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    SolrSearchResult listPage(String keyword, int pageNum, int pageSize);

    // ==================== 数据导入导出接口 ====================

    /**
     * 根据关键词查询所有匹配的专利（用于导出）
     *
     * @param keyword 搜索关键词
     * @return 专利列表
     */
    List<SearchItem> listAll(String keyword);

    /**
     * 批量导入专利数据到Solr
     *
     * @param items 专利数据列表
     * @return 导入成功的数量
     */
    int importBatch(List<SearchItem> items);
}
