package com.ruoyi.search.service.impl;

import com.ruoyi.search.domain.AdvancedSearchQuery;
import com.ruoyi.search.domain.FacetStatResult;
import com.ruoyi.search.domain.SearchItem;
import com.ruoyi.search.domain.SolrSearchResult;
import com.ruoyi.search.service.ISearchService;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Solr 搜索服务实现类
 * 直接使用 SolrClient 与 Solr 服务器交互
 *
 * @author ruoyi
 */
@Service
public class SearchServiceImpl implements ISearchService
{
    private static final Logger log = LoggerFactory.getLogger(SearchServiceImpl.class);

    /**
     * 注入 SolrClient
     * 该 Bean 在 SolrConfig 中配置创建
     */
    @Autowired
    private SolrClient solrClient;

    /**
     * 根据关键词搜索专利文档（不分页）
     *
     * @param keyword 搜索关键词
     * @return 匹配的专利文档列表
     */
    @Override
    public List<SearchItem> searchByKeyword(String keyword)
    {
        SolrSearchResult result = searchByKeyword(keyword, 1, 100);
        return result.getRecords();
    }

    /**
     * 根据关键词搜索专利文档（分页）
     *
     * @param keyword  搜索关键词
     * @param pageNum  页码（从1开始）
     * @param pageSize 每页条数
     * @return 分页搜索结果
     */
    @Override
    public SolrSearchResult searchByKeyword(String keyword, int pageNum, int pageSize)
    {
        List<SearchItem> resultList = new ArrayList<>();
        long total = 0;

        try
        {
            SolrQuery query = new SolrQuery();

            // 设置查询条件：在 title 字段中模糊匹配关键词
            query.setQuery("title:*" + keyword + "*");

            // 设置返回的字段列表
            query.setFields("id", "application_no", "publication_no", "application_date",
                    "title", "abs", "applicant", "inventor", "patent_agency",
                    "classification", "main_classification", "address", "type_name",
                    "legaltype", "source", "pdffile");

            // 设置分页参数
            query.setStart((pageNum - 1) * pageSize);
            query.setRows(pageSize);

            log.info("Solr 分页查询 - 关键词: {}, 页码: {}, 每页条数: {}", keyword, pageNum, pageSize);

            QueryResponse response = solrClient.query(query);

            SolrDocumentList documents = response.getResults();

            // 获取总记录数
            total = documents.getNumFound();

            log.info("Solr 查询结果数量: {}", total);

            // 遍历文档列表，将 SolrDocument 转换为 SearchItem 实体对象
            for (SolrDocument document : documents)
            {
                SearchItem item = new SearchItem();

                if (document.getFieldValue("id") != null)
                {
                    item.setId(document.getFieldValue("id").toString());
                }
                if (document.getFieldValue("application_no") != null)
                {
                    item.setApplicationNo(document.getFieldValue("application_no").toString());
                }
                if (document.getFieldValue("publication_no") != null)
                {
                    item.setPublicationNo(document.getFieldValue("publication_no").toString());
                }
                if (document.getFieldValue("application_date") != null)
                {
                    item.setApplicationDate(document.getFieldValue("application_date").toString());
                }
                if (document.getFieldValue("title") != null)
                {
                    item.setTitle(document.getFieldValue("title").toString());
                }
                if (document.getFieldValue("abs") != null)
                {
                    item.setAbs(document.getFieldValue("abs").toString());
                }
                if (document.getFieldValue("applicant") != null)
                {
                    item.setApplicant(document.getFieldValue("applicant").toString());
                }
                if (document.getFieldValue("inventor") != null)
                {
                    item.setInventor(document.getFieldValue("inventor").toString());
                }
                if (document.getFieldValue("patent_agency") != null)
                {
                    item.setPatentAgency(document.getFieldValue("patent_agency").toString());
                }
                if (document.getFieldValue("classification") != null)
                {
                    item.setClassification(document.getFieldValue("classification").toString());
                }
                if (document.getFieldValue("main_classification") != null)
                {
                    item.setMainClassification(document.getFieldValue("main_classification").toString());
                }
                if (document.getFieldValue("address") != null)
                {
                    item.setAddress(document.getFieldValue("address").toString());
                }
                if (document.getFieldValue("type_name") != null)
                {
                    item.setTypeName(document.getFieldValue("type_name").toString());
                }
                if (document.getFieldValue("legaltype") != null)
                {
                    item.setLegalType(document.getFieldValue("legaltype").toString());
                }
                if (document.getFieldValue("source") != null)
                {
                    item.setSource(document.getFieldValue("source").toString());
                }
                if (document.getFieldValue("pdffile") != null)
                {
                    item.setPdfFile(document.getFieldValue("pdffile").toString());
                }

                resultList.add(item);
            }

        }
        catch (SolrServerException e)
        {
            log.error("Solr 服务器异常: {}", e.getMessage());
        }
        catch (IOException e)
        {
            log.error("Solr IO 异常: {}", e.getMessage());
        }

        return new SolrSearchResult(total, resultList, pageNum, pageSize);
    }

    /**
     * 高级检索：多维度组合查询
     * 支持按标题、摘要模糊匹配，分类维度过滤，申请日期区间限定组合查询条件
     *
     * @param query 查询参数对象
     * @return 分页搜索结果
     */
    @Override
    public SolrSearchResult advancedSearch(AdvancedSearchQuery query)
    {
        List<SearchItem> resultList = new ArrayList<>();
        long total = 0;

        try
        {
            SolrQuery solrQuery = new SolrQuery();

            // 构建多维度组合查询条件
            StringBuilder queryBuilder = new StringBuilder();

            // 1. 标题模糊匹配
            if (query.getTitleKeyword() != null && !query.getTitleKeyword().trim().isEmpty())
            {
                queryBuilder.append("title:*").append(escapeSpecialChars(query.getTitleKeyword().trim())).append("*");
            }

            // 2. 摘要模糊匹配
            if (query.getAbsKeyword() != null && !query.getAbsKeyword().trim().isEmpty())
            {
                if (queryBuilder.length() > 0)
                {
                    queryBuilder.append(" AND ");
                }
                queryBuilder.append("abs:*").append(escapeSpecialChars(query.getAbsKeyword().trim())).append("*");
            }

            // 3. 分类维度过滤（支持模糊匹配）
            if (query.getClassification() != null && !query.getClassification().trim().isEmpty())
            {
                if (queryBuilder.length() > 0)
                {
                    queryBuilder.append(" AND ");
                }
                // 同时匹配 main_classification 和 classification 字段
                queryBuilder.append("(main_classification:*")
                        .append(escapeSpecialChars(query.getClassification().trim()))
                        .append("* OR classification:*")
                        .append(escapeSpecialChars(query.getClassification().trim()))
                        .append("*)");
            }

            // 4. 申请日期区间限定
            if (query.getStartDate() != null && !query.getStartDate().trim().isEmpty())
            {
                if (queryBuilder.length() > 0)
                {
                    queryBuilder.append(" AND ");
                }
                queryBuilder.append("application_date:[")
                        .append(query.getStartDate().trim())
                        .append(" TO *]");
            }
            if (query.getEndDate() != null && !query.getEndDate().trim().isEmpty())
            {
                if (queryBuilder.length() > 0)
                {
                    queryBuilder.append(" AND ");
                }
                queryBuilder.append("application_date:[* TO ")
                        .append(query.getEndDate().trim())
                        .append("]");
            }

            // 如果没有任何查询条件，默认搜索所有文档
            if (queryBuilder.length() == 0)
            {
                solrQuery.setQuery("*:*");
            }
            else
            {
                solrQuery.setQuery(queryBuilder.toString());
            }

            // 设置返回的字段列表
            solrQuery.setFields("id", "application_no", "publication_no", "application_date",
                    "title", "abs", "applicant", "inventor", "patent_agency",
                    "classification", "main_classification", "address", "type_name",
                    "legaltype", "source", "pdffile");

            // 设置分页参数
            int pageNum = query.getPageNum() != null ? query.getPageNum() : 1;
            int pageSize = query.getPageSize() != null ? query.getPageSize() : 10;
            solrQuery.setStart((pageNum - 1) * pageSize);
            solrQuery.setRows(pageSize);

            log.info("Solr 高级检索 - 查询条件: {}, 页码: {}, 每页条数: {}", queryBuilder.toString(), pageNum, pageSize);

            QueryResponse response = solrClient.query(solrQuery);

            SolrDocumentList documents = response.getResults();

            // 获取总记录数
            total = documents.getNumFound();

            log.info("Solr 高级检索结果数量: {}", total);

            // 遍历文档列表，将 SolrDocument 转换为 SearchItem 实体对象
            for (SolrDocument document : documents)
            {
                resultList.add(convertSolrDocumentToSearchItem(document));
            }

        }
        catch (SolrServerException e)
        {
            log.error("Solr 服务器异常: {}", e.getMessage());
        }
        catch (IOException e)
        {
            log.error("Solr IO 异常: {}", e.getMessage());
        }

        int pageNum = query.getPageNum() != null ? query.getPageNum() : 1;
        int pageSize = query.getPageSize() != null ? query.getPageSize() : 10;
        return new SolrSearchResult(total, resultList, pageNum, pageSize);
    }

    /**
     * 转换 SolrDocument 为 SearchItem
     */
    private SearchItem convertSolrDocumentToSearchItem(SolrDocument document)
    {
        SearchItem item = new SearchItem();

        if (document.getFieldValue("id") != null)
        {
            item.setId(document.getFieldValue("id").toString());
        }
        if (document.getFieldValue("application_no") != null)
        {
            item.setApplicationNo(document.getFieldValue("application_no").toString());
        }
        if (document.getFieldValue("publication_no") != null)
        {
            item.setPublicationNo(document.getFieldValue("publication_no").toString());
        }
        if (document.getFieldValue("application_date") != null)
        {
            item.setApplicationDate(document.getFieldValue("application_date").toString());
        }
        if (document.getFieldValue("title") != null)
        {
            item.setTitle(document.getFieldValue("title").toString());
        }
        if (document.getFieldValue("abs") != null)
        {
            item.setAbs(document.getFieldValue("abs").toString());
        }
        if (document.getFieldValue("applicant") != null)
        {
            item.setApplicant(document.getFieldValue("applicant").toString());
        }
        if (document.getFieldValue("inventor") != null)
        {
            item.setInventor(document.getFieldValue("inventor").toString());
        }
        if (document.getFieldValue("patent_agency") != null)
        {
            item.setPatentAgency(document.getFieldValue("patent_agency").toString());
        }
        if (document.getFieldValue("classification") != null)
        {
            item.setClassification(document.getFieldValue("classification").toString());
        }
        if (document.getFieldValue("main_classification") != null)
        {
            item.setMainClassification(document.getFieldValue("main_classification").toString());
        }
        if (document.getFieldValue("address") != null)
        {
            item.setAddress(document.getFieldValue("address").toString());
        }
        if (document.getFieldValue("type_name") != null)
        {
            item.setTypeName(document.getFieldValue("type_name").toString());
        }
        if (document.getFieldValue("legaltype") != null)
        {
            item.setLegalType(document.getFieldValue("legaltype").toString());
        }
        if (document.getFieldValue("source") != null)
        {
            item.setSource(document.getFieldValue("source").toString());
        }
        if (document.getFieldValue("pdffile") != null)
        {
            item.setPdfFile(document.getFieldValue("pdffile").toString());
        }

        return item;
    }

    /**
     * 转义 Solr 查询特殊字符
     */
    private String escapeSpecialChars(String input)
    {
        if (input == null || input.isEmpty())
        {
            return input;
        }
        // Solr 特殊字符：+ - && || ! ( ) { } [ ] ^ " ~ * ? : \ /
        String specialChars = "+-&|!(){}[]^\"~*?:\\-/";
        StringBuilder escaped = new StringBuilder();
        for (char c : input.toCharArray())
        {
            if (specialChars.indexOf(c) >= 0)
            {
                escaped.append("\\");
            }
            escaped.append(c);
        }
        return escaped.toString();
    }

    // ==================== Facet 聚合统计方法 ====================

    /**
     * 按省份统计专利数量（用于柱状图）
     * 使用 Solr Facet 查询实现
     *
     * @param keyword 搜索关键词（可选，为空则统计全部）
     * @param limit   返回前N个省份
     * @return 省份统计结果
     */
    @Override
    public FacetStatResult statByProvince(String keyword, int limit)
    {
        List<FacetStatResult.FacetItem> items = new ArrayList<>();
        long total = 0;

        try
        {
            SolrQuery query = new SolrQuery();

            // 设置查询条件
            if (keyword != null && !keyword.trim().isEmpty())
            {
                query.setQuery("title:*" + escapeSpecialChars(keyword.trim()) + "*");
            }
            else
            {
                query.setQuery("*:*");
            }

            // 启用 Facet 统计
            query.setFacet(true);
            query.addFacetField("regionsecond_s");
            query.setFacetLimit(limit > 0 ? limit : 20);
            query.setFacetMinCount(1);
            query.setRows(0); // 不需要返回文档，只需要统计

            log.info("Solr 省份统计 - 关键词: {}, 限制: {}", keyword, limit);

            QueryResponse response = solrClient.query(query);

            // 解析 Facet 结果
            List<FacetField> facetFields = response.getFacetFields();
            if (facetFields != null && !facetFields.isEmpty())
            {
                FacetField provinceField = facetFields.get(0);
                List<FacetField.Count> counts = provinceField.getValues();

                if (counts != null)
                {
                    for (FacetField.Count count : counts)
                    {
                        String province = count.getName();
                        long countValue = count.getCount();
                        if (province != null && !province.trim().isEmpty())
                        {
                            items.add(new FacetStatResult.FacetItem(province, countValue));
                            total += countValue;
                        }
                    }
                }
            }

            // 按数量降序排序
            items.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));

            // 计算百分比
            if (total > 0)
            {
                for (FacetStatResult.FacetItem item : items)
                {
                    item.setPercentage(Math.round(item.getCount() * 100.0 / total * 100.0) / 100.0);
                }
            }

            log.info("Solr 省份统计结果: 共 {} 个省份, 总计 {} 条", items.size(), total);
        }
        catch (SolrServerException e)
        {
            log.error("Solr 省份统计服务器异常: {}", e.getMessage());
        }
        catch (IOException e)
        {
            log.error("Solr 省份统计 IO 异常: {}", e.getMessage());
        }

        return new FacetStatResult("province", items, total);
    }

    /**
     * 按类型统计专利数量（用于饼图）
     * 使用 Solr Facet 查询实现
     *
     * @param keyword 搜索关键词（可选，为空则统计全部）
     * @return 类型统计结果
     */
    @Override
    public FacetStatResult statByType(String keyword)
    {
        List<FacetStatResult.FacetItem> items = new ArrayList<>();
        long total = 0;

        try
        {
            SolrQuery query = new SolrQuery();

            // 设置查询条件
            if (keyword != null && !keyword.trim().isEmpty())
            {
                query.setQuery("title:*" + escapeSpecialChars(keyword.trim()) + "*");
            }
            else
            {
                query.setQuery("*:*");
            }

            // 启用 Facet 统计
            query.setFacet(true);
            query.addFacetField("type_name");
            query.setFacetLimit(20);
            query.setFacetMinCount(1);
            query.setRows(0);

            log.info("Solr 类型统计 - 关键词: {}", keyword);

            QueryResponse response = solrClient.query(query);

            // 解析 Facet 结果
            List<FacetField> facetFields = response.getFacetFields();
            if (facetFields != null && !facetFields.isEmpty())
            {
                FacetField typeField = facetFields.get(0);
                List<FacetField.Count> counts = typeField.getValues();

                if (counts != null)
                {
                    for (FacetField.Count count : counts)
                    {
                        String typeName = count.getName();
                        long countValue = count.getCount();
                        if (typeName != null && !typeName.trim().isEmpty())
                        {
                            items.add(new FacetStatResult.FacetItem(typeName, countValue));
                            total += countValue;
                        }
                    }
                }
            }

            // 按数量降序排序
            items.sort((a, b) -> Long.compare(b.getCount(), a.getCount()));

            // 计算百分比
            if (total > 0)
            {
                for (FacetStatResult.FacetItem item : items)
                {
                    item.setPercentage(Math.round(item.getCount() * 100.0 / total * 100.0) / 100.0);
                }
            }

            log.info("Solr 类型统计结果: 共 {} 个类型, 总计 {} 条", items.size(), total);
        }
        catch (SolrServerException e)
        {
            log.error("Solr 类型统计服务器异常: {}", e.getMessage());
        }
        catch (IOException e)
        {
            log.error("Solr 类型统计 IO 异常: {}", e.getMessage());
        }

        return new FacetStatResult("type", items, total);
    }

    /**
     * 按申请年份统计专利数量（用于时间趋势图）
     * 使用 Solr Facet 查询实现
     *
     * @param keyword   搜索关键词（可选，为空则统计全部）
     * @param startYear 起始年份
     * @param endYear   结束年份
     * @return 年份统计结果
     */
    @Override
    public FacetStatResult statByYear(String keyword, int startYear, int endYear)
    {
        List<FacetStatResult.FacetItem> items = new ArrayList<>();
        long total = 0;

        // 设置默认年份范围
        if (startYear <= 0) startYear = 2000;
        if (endYear <= 0 || endYear < startYear) endYear = 2024;

        try
        {
            SolrQuery query = new SolrQuery();

            // 设置查询条件
            if (keyword != null && !keyword.trim().isEmpty())
            {
                query.setQuery("title:*" + escapeSpecialChars(keyword.trim()) + "*");
            }
            else
            {
                query.setQuery("*:*");
            }

            // 添加年份范围过滤
            String yearRangeFilter = String.format("application_date:[%d-01-01T00:00:00Z TO %d-12-31T23:59:59Z]",
                    startYear, endYear);
            query.addFilterQuery(yearRangeFilter);

            // 启用 Facet 统计
            query.setFacet(true);
            query.setFacetLimit(endYear - startYear + 1);
            query.setFacetMinCount(0);
            query.setRows(0);

            log.info("Solr 年份统计 - 关键词: {}, 年份范围: {}-{}", keyword, startYear, endYear);

            QueryResponse response = solrClient.query(query);

            // 解析 Facet 结果 - 直接使用备用方案查询所有结果并手动按年份统计
            Map<String, Integer> yearCountMap = new HashMap<>();

            // 查询所有结果并手动按年份统计
            SolrQuery backupQuery = new SolrQuery();
            if (keyword != null && !keyword.trim().isEmpty())
            {
                backupQuery.setQuery("title:*" + escapeSpecialChars(keyword.trim()) + "*");
            }
            else
            {
                backupQuery.setQuery("*:*");
            }
            backupQuery.addFilterQuery(yearRangeFilter);
            backupQuery.setFields("application_date");
            backupQuery.setRows(10000);

            QueryResponse backupResponse = solrClient.query(backupQuery);
            SolrDocumentList documents = backupResponse.getResults();

            for (SolrDocument doc : documents)
            {
                Object dateValue = doc.getFieldValue("application_date");
                if (dateValue != null)
                {
                    String dateStr = dateValue.toString();
                    // 提取年份
                    int year = extractYear(dateStr);
                    if (year >= startYear && year <= endYear)
                    {
                        yearCountMap.merge(String.valueOf(year), 1, Integer::sum);
                    }
                }
            }

            // 转换为列表并按年份排序
            for (int year = startYear; year <= endYear; year++)
            {
                String yearStr = String.valueOf(year);
                int count = yearCountMap.getOrDefault(yearStr, 0);
                items.add(new FacetStatResult.FacetItem(yearStr, count));
                total += count;
            }

            // 按年份升序排序
            items.sort(Comparator.comparingInt(a -> Integer.parseInt(a.getName())));

            log.info("Solr 年份统计结果: 共 {} 个年份, 总计 {} 条", items.size(), total);
        }
        catch (SolrServerException e)
        {
            log.error("Solr 年份统计服务器异常: {}", e.getMessage());
        }
        catch (IOException e)
        {
            log.error("Solr 年份统计 IO 异常: {}", e.getMessage());
        }

        return new FacetStatResult("year", items, total);
    }

    /**
     * 从日期字符串中提取年份
     */
    private int extractYear(String dateStr)
    {
        if (dateStr == null || dateStr.isEmpty())
        {
            return 0;
        }
        try
        {
            // 处理 ISO 格式日期：2020-01-01T00:00:00Z 或 2020-01-01
            if (dateStr.length() >= 4)
            {
                return Integer.parseInt(dateStr.substring(0, 4));
            }
        }
        catch (NumberFormatException e)
        {
            log.warn("无法从日期字符串提取年份: {}", dateStr);
        }
        return 0;
    }
}
