package com.ruoyi.business.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.business.domain.BizReconciliation;
import com.ruoyi.business.domain.BizReconciliationExportVo;
import com.ruoyi.business.service.IBizReconciliationService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;

/**
 * 对账关联表Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/business/reconciliation")
public class BizReconciliationController extends BaseController
{
    @Autowired
    private IBizReconciliationService bizReconciliationService;

    /**
     * 查询对账关联表列表
     */
    @GetMapping("/list")
    public TableDataInfo list(BizReconciliation bizReconciliation)
    {
        startPage();
        List<BizReconciliation> list = bizReconciliationService.selectBizReconciliationList(bizReconciliation);
        return getDataTable(list);
    }

    /**
     * 导出对账关联表列表
     */
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizReconciliation bizReconciliation)
    {
        List<BizReconciliation> list = bizReconciliationService.selectBizReconciliationList(bizReconciliation);
        List<BizReconciliationExportVo> rows = new ArrayList<>(list.size());
        for (BizReconciliation item : list)
        {
            rows.add(BizReconciliationExportVo.from(item));
        }
        ExcelUtil<BizReconciliationExportVo> util = new ExcelUtil<BizReconciliationExportVo>(BizReconciliationExportVo.class);
        util.exportExcel(response, rows, "对账列表");
    }

    /**
     * 获取对账关联表详细信息
     */
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(bizReconciliationService.selectBizReconciliationById(id));
    }

    /**
     * 新增对账关联表
     */
    @PostMapping
    public AjaxResult add(@RequestBody BizReconciliation bizReconciliation)
    {
        return toAjax(bizReconciliationService.insertBizReconciliation(bizReconciliation));
    }

    /**
     * 修改对账关联表
     */
    @PutMapping
    public AjaxResult edit(@RequestBody BizReconciliation bizReconciliation)
    {
        return toAjax(bizReconciliationService.updateBizReconciliation(bizReconciliation));
    }

    /**
     * 删除对账关联表
     */
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(bizReconciliationService.deleteBizReconciliationByIds(ids));
    }
    
    /**
     * 批量自动对账
     */
    @PostMapping("/auto-reconciliation")
    public AjaxResult batchAutoReconciliation()
    {
        Map<String, Object> result = bizReconciliationService.batchAutoReconciliation();
        return AjaxResult.success("自动对账完成", result);
    }
    
    /**
     * 手动对账
     */
    @PostMapping("/manual-reconciliation")
    public AjaxResult manualReconciliation(@RequestBody Map<String, Object> params, HttpServletRequest request)
    {
        // 检查参数是否为null
        if (params.get("guaranteeBusinessId") == null) {
            return AjaxResult.error("手动对账失败，缺少guaranteeBusinessId参数");
        }
        if (params.get("bankTransactionId") == null) {
            return AjaxResult.error("手动对账失败，缺少bankTransactionId参数");
        }
        
        Long guaranteeBusinessId;
        Long bankTransactionId;
        try {
            guaranteeBusinessId = Long.valueOf(params.get("guaranteeBusinessId").toString());
            bankTransactionId = Long.valueOf(params.get("bankTransactionId").toString());
        } catch (NumberFormatException e) {
            return AjaxResult.error("手动对账失败，参数格式错误：" + e.getMessage());
        }
        
        String operator = (String) params.get("operator");
        String operatorIp = getClientIP(request);
        
        System.out.println("手动对账参数：guaranteeBusinessId=" + guaranteeBusinessId + ", bankTransactionId=" + bankTransactionId + ", operator=" + operator);
        
        int result = bizReconciliationService.manualReconciliation(guaranteeBusinessId, bankTransactionId, operator, operatorIp);
        if (result > 0) {
            return AjaxResult.success("手动对账成功");
        } else {
            return AjaxResult.error("手动对账失败，请检查担保业务和银行流水是否存在，或担保业务已对账");
        }
    }
    
    /**
     * 解绑对账
     */
    @PostMapping("/unbind/{id}")
    public AjaxResult unbindReconciliation(@PathVariable Long id, @RequestBody Map<String, String> params, HttpServletRequest request)
    {
        String operator = params.get("operator");
        String operatorIp = getClientIP(request);
        
        int result = bizReconciliationService.unbindReconciliation(id, operator, operatorIp);
        if (result > 0) {
            return AjaxResult.success("解绑成功");
        } else {
            return AjaxResult.error("解绑失败");
        }
    }
    
    /**
     * 统计对账状态
     */
    @GetMapping("/status-count")
    public AjaxResult countStatus()
    {
        Map<String, Integer> statusCount = bizReconciliationService.countReconciliationStatus();
        return AjaxResult.success(statusCount);
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
