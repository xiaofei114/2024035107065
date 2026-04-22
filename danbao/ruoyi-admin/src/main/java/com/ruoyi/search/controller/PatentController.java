package com.ruoyi.search.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.search.domain.SearchItem;
import com.ruoyi.search.domain.SolrSearchResult;
import com.ruoyi.search.service.ISearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 专利数据管理控制器
 * 提供Solr数据的增删改查接口
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/api/patent")
public class PatentController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(PatentController.class);

    @Autowired
    private ISearchService searchService;

    /**
     * 分页查询专利列表（数据管理用）
     *
     * @param keyword  标题关键词
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return AjaxResult 分页结果
     */
    @GetMapping("/list")
    public AjaxResult listPage(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize)
    {
        log.info("数据列表查询请求 - 关键词: {}, 页码: {}, 每页: {}", keyword, pageNum, pageSize);

        SolrSearchResult result = searchService.listPage(keyword, pageNum, pageSize);

        Map<String, Object> data = new HashMap<>();
        data.put("list", result.getRecords());
        data.put("total", result.getTotal());
        data.put("pageNum", result.getPageNum());
        data.put("pageSize", result.getPageSize());
        data.put("totalPages", (int) Math.ceil((double) result.getTotal() / result.getPageSize()));

        return success(data);
    }

    /**
     * 根据ID获取专利详情
     *
     * @param id 专利ID
     * @return AjaxResult 专利详情
     */
    @GetMapping("/{id}")
    public AjaxResult getById(@PathVariable String id)
    {
        log.info("获取专利详情请求 - ID: {}", id);

        SearchItem item = searchService.getById(id);
        if (item == null)
        {
            return error("专利不存在");
        }
        return success(item);
    }

    /**
     * 添加专利
     *
     * @param item 专利信息
     * @return AjaxResult 操作结果
     */
    @PostMapping
    public AjaxResult add(@RequestBody SearchItem item)
    {
        log.info("添加专利请求 - 标题: {}", item.getTitle());

        if (item.getTitle() == null || item.getTitle().trim().isEmpty())
        {
            return error("专利标题不能为空");
        }

        boolean success = searchService.add(item);
        if (success)
        {
            return success("添加成功");
        }
        else
        {
            return error("添加失败");
        }
    }

    /**
     * 更新专利
     *
     * @param item 专利信息
     * @return AjaxResult 操作结果
     */
    @PutMapping
    public AjaxResult update(@RequestBody SearchItem item)
    {
        log.info("更新专利请求 - ID: {}, 标题: {}", item.getId(), item.getTitle());

        if (item.getId() == null || item.getId().trim().isEmpty())
        {
            return error("专利ID不能为空");
        }

        boolean success = searchService.update(item);
        if (success)
        {
            return success("更新成功");
        }
        else
        {
            return error("更新失败");
        }
    }

    /**
     * 删除专利
     *
     * @param id 专利ID
     * @return AjaxResult 操作结果
     */
    @DeleteMapping("/{id}")
    public AjaxResult delete(@PathVariable String id)
    {
        log.info("删除专利请求 - ID: {}", id);

        boolean success = searchService.delete(id);
        if (success)
        {
            return success("删除成功");
        }
        else
        {
            return error("删除失败");
        }
    }

    /**
     * 批量删除专利
     *
     * @param ids 专利ID列表
     * @return AjaxResult 操作结果
     */
    @DeleteMapping("/batch")
    public AjaxResult deleteBatch(@RequestBody List<String> ids)
    {
        log.info("批量删除专利请求 - 数量: {}", ids != null ? ids.size() : 0);

        if (ids == null || ids.isEmpty())
        {
            return error("请选择要删除的数据");
        }

        boolean success = searchService.deleteBatch(ids);
        if (success)
        {
            return success("批量删除成功");
        }
        else
        {
            return error("批量删除失败");
        }
    }
}
