package com.ruoyi.search.utils;

import com.ruoyi.search.domain.SearchItem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel导入工具类
 * 用于解析Excel文件并转换为Solr专利文档
 */
public class ExcelImportUtil
{

    /**
     * 从Excel文件解析专利数据
     *
     * @param file Excel文件
     * @return 专利数据列表
     */
    public static List<SearchItem> parsePatentExcel(MultipartFile file) throws IOException
    {
        List<SearchItem> items = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is))
        {
            Sheet sheet = workbook.getSheetAt(0);

            // 从第二行开始读取（跳过标题行）
            for (int i = 1; i <= sheet.getLastRowNum(); i++)
            {
                Row row = sheet.getRow(i);
                if (row == null)
                {
                    continue;
                }

                SearchItem item = new SearchItem();

                // 根据列索引读取数据
                item.setId(getCellValue(row.getCell(0)));
                item.setApplicationNo(getCellValue(row.getCell(1)));
                item.setPublicationNo(getCellValue(row.getCell(2)));
                item.setApplicationDate(getCellValue(row.getCell(3)));
                item.setTitle(getCellValue(row.getCell(4)));
                item.setAbs(getCellValue(row.getCell(5)));
                item.setPatClaim(getCellValue(row.getCell(6)));
                item.setApplicant(getCellValue(row.getCell(7)));
                item.setInventor(getCellValue(row.getCell(8)));
                item.setTypeName(getCellValue(row.getCell(9)));
                item.setLegalType(getCellValue(row.getCell(10)));
                item.setClassification(getCellValue(row.getCell(11)));
                item.setMainClassification(getCellValue(row.getCell(12)));
                item.setPatentAgency(getCellValue(row.getCell(13)));
                item.setAddress(getCellValue(row.getCell(14)));
                item.setSource(getCellValue(row.getCell(15)));

                // 如果ID为空，生成UUID
                if (item.getId() == null || item.getId().trim().isEmpty())
                {
                    item.setId(java.util.UUID.randomUUID().toString());
                }

                items.add(item);
            }
        }

        return items;
    }

    /**
     * 获取单元格值
     */
    private static String getCellValue(Cell cell)
    {
        if (cell == null)
        {
            return "";
        }

        switch (cell.getCellType())
        {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell))
                {
                    return cell.getDateCellValue().toString();
                }
                else
                {
                    // 处理数字，避免科学计数法
                    double value = cell.getNumericCellValue();
                    if (value == (long) value)
                    {
                        return String.valueOf((long) value);
                    }
                    return String.valueOf(value);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    /**
     * 验证Excel文件格式
     *
     * @param file 上传的文件
     * @return 验证结果
     */
    public static boolean validateExcelFile(MultipartFile file)
    {
        if (file == null || file.isEmpty())
        {
            return false;
        }

        String filename = file.getOriginalFilename();
        if (filename == null)
        {
            return false;
        }

        // 支持 .xlsx 和 .xls 格式
        return filename.endsWith(".xlsx") || filename.endsWith(".xls");
    }
}
