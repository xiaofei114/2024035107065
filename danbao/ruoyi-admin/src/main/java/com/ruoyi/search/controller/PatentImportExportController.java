package com.ruoyi.search.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.search.domain.SearchItem;
import com.ruoyi.search.service.ISearchService;
import com.ruoyi.search.utils.ExcelExportUtil;
import com.ruoyi.search.utils.ExcelImportUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 专利数据导入导出控制器
 * 提供Excel导入导出功能
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/api/patent")
public class PatentImportExportController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(PatentImportExportController.class);

    @Autowired
    private ISearchService searchService;

    /**
     * 导出专利数据到Excel
     *
     * @param keyword  搜索关键词（可选）
     * @param response HTTP响应对象
     */
    @GetMapping("/export")
    public void exportExcel(
            @RequestParam(value = "keyword", required = false) String keyword,
            HttpServletResponse response)
    {
        try
        {
            log.info("导出请求 - 关键词: {}", keyword);

            // 查询数据
            List<SearchItem> items = searchService.listAll(keyword);

            if (items.isEmpty())
            {
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().write("{\"code\":500,\"msg\":\"没有数据可导出\"}");
                return;
            }

            // 生成文件名
            String filename = "专利数据_" + System.currentTimeMillis();

            // 导出Excel
            ExcelExportUtil.exportPatentList(items, response, filename);

            log.info("导出成功 - 共 {} 条数据", items.size());
        }
        catch (IOException e)
        {
            log.error("导出失败", e);
            try
            {
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().write("{\"code\":500,\"msg\":\"导出失败\"}");
            }
            catch (IOException ex)
            {
                log.error("写入错误响应失败", ex);
            }
        }
    }

    /**
     * 导入专利数据预览
     *
     * @param file Excel文件
     * @return 预览数据
     */
    @PostMapping("/import/preview")
    public AjaxResult importPreview(@RequestParam("file") MultipartFile file)
    {
        try
        {
            log.info("导入预览请求 - 文件名: {}", file.getOriginalFilename());

            // 验证文件格式
            if (!ExcelImportUtil.validateExcelFile(file))
            {
                return error("请上传正确的Excel文件（.xlsx或.xls格式）");
            }

            // 解析Excel
            List<SearchItem> items = ExcelImportUtil.parsePatentExcel(file);

            if (items.isEmpty())
            {
                return error("Excel文件中没有数据");
            }

            // 返回预览数据（只返回前10条）
            List<SearchItem> previewItems = items.size() > 10 ? items.subList(0, 10) : items;

            Map<String, Object> result = new HashMap<>();
            result.put("total", items.size());
            result.put("preview", previewItems);

            return success(result);
        }
        catch (IOException e)
        {
            log.error("导入预览失败", e);
            return error("文件解析失败：" + e.getMessage());
        }
    }

    /**
     * 确认导入专利数据
     *
     * @param file Excel文件
     * @return 导入结果
     */
    @PostMapping("/import")
    public AjaxResult importExcel(@RequestParam("file") MultipartFile file)
    {
        try
        {
            log.info("导入请求 - 文件名: {}", file.getOriginalFilename());

            // 验证文件格式
            if (!ExcelImportUtil.validateExcelFile(file))
            {
                return error("请上传正确的Excel文件（.xlsx或.xls格式）");
            }

            // 解析Excel
            List<SearchItem> items = ExcelImportUtil.parsePatentExcel(file);

            if (items.isEmpty())
            {
                return error("Excel文件中没有数据");
            }

            // 批量导入
            int successCount = searchService.importBatch(items);

            Map<String, Object> result = new HashMap<>();
            result.put("total", items.size());
            result.put("success", successCount);
            result.put("failed", items.size() - successCount);

            if (successCount == items.size())
            {
                return AjaxResult.success("成功导入 " + successCount + " 条数据").put("data", result);
            }
            else
            {
                return AjaxResult.success("部分导入成功：成功 " + successCount + " 条，失败 " + (items.size() - successCount) + " 条").put("data", result);
            }
        }
        catch (IOException e)
        {
            log.error("导入失败", e);
            return error("导入失败：" + e.getMessage());
        }
    }

    /**
     * 下载导入模板
     *
     * @param response HTTP响应对象
     */
    @GetMapping("/import/template")
    public void downloadTemplate(HttpServletResponse response)
    {
        try
        {
            log.info("下载导入模板请求");

            // 创建示例数据
            SearchItem example = new SearchItem();
            example.setId("示例ID");
            example.setApplicationNo("CN202010000000.0");
            example.setPublicationNo("CN111111111A");
            example.setApplicationDate("2020-01-01");
            example.setTitle("示例专利标题");
            example.setAbs("这是专利摘要的示例内容");
            example.setPatClaim("这是权力要求书的示例内容");
            example.setApplicant("示例申请人");
            example.setInventor("示例发明人");
            example.setTypeName("发明专利");
            example.setLegalType("有效");
            example.setClassification("G06F17/30");
            example.setMainClassification("G06F");
            example.setPatentAgency("示例代理机构");
            example.setAddress("示例地址");
            example.setSource("示例来源");

            // 导出模板
            ExcelExportUtil.exportPatentList(java.util.Collections.singletonList(example), response, "专利导入模板");
        }
        catch (IOException e)
        {
            log.error("下载模板失败", e);
            try
            {
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().write("{\"code\":500,\"msg\":\"下载模板失败\"}");
            }
            catch (IOException ex)
            {
                log.error("写入错误响应失败", ex);
            }
        }
    }
}
