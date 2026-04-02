package com.ruoyi.business.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.business.domain.BizBankFlow;
import com.ruoyi.business.service.IBizBankFlowService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;

/**
 * 账单台账Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/business/bankflow")
public class BizBankFlowController extends BaseController
{
    @Autowired
    private IBizBankFlowService bizBankFlowService;

    /**
     * 查询账单台账列表
     */
    @PreAuthorize("@ss.hasPermi('business:bankflow:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizBankFlow bizBankFlow)
    {
        startPage();
        List<BizBankFlow> list = bizBankFlowService.selectBizBankFlowList(bizBankFlow);
        return getDataTable(list);
    }

    /**
     * 导出账单台账列表
     */
    @PreAuthorize("@ss.hasPermi('business:bankflow:export')")
    @Log(title = "账单台账", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizBankFlow bizBankFlow)
    {
        List<BizBankFlow> list = bizBankFlowService.selectBizBankFlowList(bizBankFlow);
        ExcelUtil<BizBankFlow> util = new ExcelUtil<BizBankFlow>(BizBankFlow.class);
        util.exportExcel(response, list, "账单台账数据");
    }

    /**
     * 获取账单台账详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:bankflow:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(bizBankFlowService.selectBizBankFlowById(id));
    }

    /**
     * 新增账单台账
     */
    @PreAuthorize("@ss.hasPermi('business:bankflow:add')")
    @Log(title = "账单台账", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody BizBankFlow bizBankFlow)
    {
        bizBankFlow.setCreateBy(getUsername());
        return toAjax(bizBankFlowService.insertBizBankFlow(bizBankFlow));
    }

    /**
     * 修改账单台账
     */
    @PreAuthorize("@ss.hasPermi('business:bankflow:edit')")
    @Log(title = "账单台账", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody BizBankFlow bizBankFlow)
    {
        bizBankFlow.setUpdateBy(getUsername());
        return toAjax(bizBankFlowService.updateBizBankFlow(bizBankFlow));
    }

    /**
     * 删除账单台账
     */
    @PreAuthorize("@ss.hasPermi('business:bankflow:remove')")
    @Log(title = "账单台账", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(bizBankFlowService.deleteBizBankFlowByIds(ids));
    }

    /**
     * 查询未被绑定的银行流水列表
     */
    @PreAuthorize("@ss.hasPermi('business:bankflow:list')")
    @GetMapping("/unbound")
    public TableDataInfo unbound(BizBankFlow bizBankFlow)
    {
        startPage();
        List<BizBankFlow> list = bizBankFlowService.selectUnboundBankFlowList(bizBankFlow);
        return getDataTable(list);
    }
}
