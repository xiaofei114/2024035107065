package com.ruoyi.search.utils;

import com.ruoyi.search.domain.SearchItem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Excel导出工具类
 * 用于Solr专利数据的导出
 */
public class ExcelExportUtil
{

    /**
     * 导出专利数据到Excel
     *
     * @param items    专利数据列表
     * @param response HTTP响应对象
     * @param filename 文件名
     */
    public static void exportPatentList(List<SearchItem> items, HttpServletResponse response, String filename)
            throws IOException
    {
        // 创建工作簿
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("专利数据");

        // 创建标题行样式
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);

        // 创建标题行
        String[] headers = {
                "ID", "申请号", "公开号", "申请日期", "专利标题", "摘要",
                "权力要求书", "申请人", "发明人", "专利类型", "法律状态",
                "分类号", "主分类号", "专利代理机构", "地址", "来源"
        };

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++)
        {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
            // 设置列宽
            sheet.setColumnWidth(i, 20 * 256);
        }

        // 填充数据行
        int rowNum = 1;
        for (SearchItem item : items)
        {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(getStringValue(item.getId()));
            row.createCell(1).setCellValue(getStringValue(item.getApplicationNo()));
            row.createCell(2).setCellValue(getStringValue(item.getPublicationNo()));
            row.createCell(3).setCellValue(getStringValue(item.getApplicationDate()));
            row.createCell(4).setCellValue(getStringValue(item.getTitle()));
            row.createCell(5).setCellValue(getStringValue(item.getAbs()));
            row.createCell(6).setCellValue(getStringValue(item.getPatClaim()));
            row.createCell(7).setCellValue(getStringValue(item.getApplicant()));
            row.createCell(8).setCellValue(getStringValue(item.getInventor()));
            row.createCell(9).setCellValue(getStringValue(item.getTypeName()));
            row.createCell(10).setCellValue(getStringValue(item.getLegalType()));
            row.createCell(11).setCellValue(getStringValue(item.getClassification()));
            row.createCell(12).setCellValue(getStringValue(item.getMainClassification()));
            row.createCell(13).setCellValue(getStringValue(item.getPatentAgency()));
            row.createCell(14).setCellValue(getStringValue(item.getAddress()));
            row.createCell(15).setCellValue(getStringValue(item.getSource()));

            // 应用数据样式
            for (int i = 0; i < headers.length; i++)
            {
                row.getCell(i).setCellStyle(dataStyle);
            }
        }

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String encodedFilename = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFilename + ".xlsx");

        // 写入响应流
        workbook.write(response.getOutputStream());
        workbook.close();
    }

    /**
     * 创建标题行样式
     */
    private static CellStyle createHeaderStyle(Workbook workbook)
    {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        return style;
    }

    /**
     * 创建数据行样式
     */
    private static CellStyle createDataStyle(Workbook workbook)
    {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setWrapText(true);
        return style;
    }

    /**
     * 获取字符串值，处理null
     */
    private static String getStringValue(String value)
    {
        return value == null ? "" : value;
    }
}
