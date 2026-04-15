package com.ruoyi.search.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.search.domain.AdvancedSearchQuery;
import com.ruoyi.search.domain.FacetStatResult;
import com.ruoyi.search.domain.SearchItem;
import com.ruoyi.search.domain.SolrSearchResult;
import com.ruoyi.search.service.ISearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Solr 搜索控制器
 * 提供全文检索接口
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/api/search")
public class SearchController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private ISearchService searchService;

    /**
     * 根据关键词搜索文档（支持分页）
     *
     * @param keyword  搜索关键词
     * @param pageNum  页码（默认1）
     * @param pageSize 每页条数（默认10）
     * @return AjaxResult 统一响应结果
     */
    @GetMapping("/list")
    public AjaxResult list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize)
    {
        // 参数校验
        if (keyword == null || keyword.trim().isEmpty())
        {
            return error("搜索关键词不能为空");
        }

        // 调用 Service 层进行分页搜索
        SolrSearchResult result = searchService.searchByKeyword(keyword.trim(), pageNum, pageSize);

        // 封装返回数据，兼容前端分页格式
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("records", result.getRecords());
        responseData.put("total", result.getTotal());
        responseData.put("pageNum", result.getPageNum());
        responseData.put("pageSize", result.getPageSize());

        return success(responseData);
    }

    /**
     * 高级检索：多维度组合查询
     * 支持按标题、摘要模糊匹配，分类维度过滤，申请日期区间限定组合查询条件
     *
     * @param titleKeyword    标题关键词（模糊匹配）
     * @param absKeyword      摘要关键词（模糊匹配）
     * @param classification  分类号（模糊匹配）
     * @param startDate       申请日期开始时间（格式：yyyy-MM-dd）
     * @param endDate         申请日期结束时间（格式：yyyy-MM-dd）
     * @param pageNum         页码（默认1）
     * @param pageSize        每页条数（默认10）
     * @return AjaxResult 统一响应结果
     */
    @GetMapping("/advanced")
    public AjaxResult advancedSearch(
            @RequestParam(value = "titleKeyword", required = false) String titleKeyword,
            @RequestParam(value = "absKeyword", required = false) String absKeyword,
            @RequestParam(value = "classification", required = false) String classification,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize)
    {
        // 构建查询参数对象
        AdvancedSearchQuery query = new AdvancedSearchQuery();
        query.setTitleKeyword(titleKeyword);
        query.setAbsKeyword(absKeyword);
        query.setClassification(classification);
        query.setStartDate(startDate);
        query.setEndDate(endDate);
        query.setPageNum(pageNum);
        query.setPageSize(pageSize);

        log.info("高级检索请求参数: {}", query);

        // 调用 Service 层进行高级检索
        SolrSearchResult result = searchService.advancedSearch(query);

        // 封装返回数据，兼容前端分页格式
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("records", result.getRecords());
        responseData.put("total", result.getTotal());
        responseData.put("pageNum", result.getPageNum());
        responseData.put("pageSize", result.getPageSize());

        return success(responseData);
    }

    /**
     * 获取专利分类列表（用于下拉选择）
     * 从 Solr 中获取所有不同的分类号
     *
     * @return AjaxResult 分类列表
     */
    @GetMapping("/classifications")
    public AjaxResult getClassifications()
    {
        // 返回常用分类（实际项目中可以从数据库或 Solr facet 获取）
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("classifications", new String[]{
                "H01", "H02", "H04", "H05",  // 电学
                "G06", "G07", "G08", "G09",  // 计算/测量
                "A61", "A62", "A63",         // 医学
                "B01", "B02", "B23",         // 机械/加工
                "C01", "C02", "C07", "C08",  // 化学
                "F01", "F02", "F03", "F04"   // 机械工程
        });
        return success(responseData);
    }

    // ==================== Facet 聚合统计接口 ====================

    /**
     * 按省份统计专利数量（用于柱状图）
     * 使用 Solr Facet 查询实现
     *
     * @param keyword 搜索关键词（可选，为空则统计全部）
     * @param limit   返回前N个省份（默认20）
     * @return AjaxResult 省份统计结果
     */
    @GetMapping("/stat/province")
    public AjaxResult statByProvince(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "limit", defaultValue = "20") Integer limit)
    {
        log.info("省份统计请求 - 关键词: {}, 限制: {}", keyword, limit);

        FacetStatResult result = searchService.statByProvince(keyword, limit);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("dimension", result.getDimension());
        responseData.put("items", result.getItems());
        responseData.put("total", result.getTotal());

        return success(responseData);
    }

    /**
     * 按类型统计专利数量（用于饼图）
     * 使用 Solr Facet 查询实现
     *
     * @param keyword 搜索关键词（可选，为空则统计全部）
     * @return AjaxResult 类型统计结果
     */
    @GetMapping("/stat/type")
    public AjaxResult statByType(
            @RequestParam(value = "keyword", required = false) String keyword)
    {
        log.info("类型统计请求 - 关键词: {}", keyword);

        FacetStatResult result = searchService.statByType(keyword);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("dimension", result.getDimension());
        responseData.put("items", result.getItems());
        responseData.put("total", result.getTotal());

        return success(responseData);
    }

    /**
     * 按申请年份统计专利数量（用于时间趋势图）
     * 使用 Solr Facet 查询实现
     *
     * @param keyword   搜索关键词（可选，为空则统计全部）
     * @param startYear 起始年份（默认2000）
     * @param endYear   结束年份（默认2024）
     * @return AjaxResult 年份统计结果
     */
    @GetMapping("/stat/year")
    public AjaxResult statByYear(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "startYear", defaultValue = "2000") Integer startYear,
            @RequestParam(value = "endYear", defaultValue = "2024") Integer endYear)
    {
        log.info("年份统计请求 - 关键词: {}, 年份范围: {}-{}", keyword, startYear, endYear);

        FacetStatResult result = searchService.statByYear(keyword, startYear, endYear);

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("dimension", result.getDimension());
        responseData.put("items", result.getItems());
        responseData.put("total", result.getTotal());

        return success(responseData);
    }
}
