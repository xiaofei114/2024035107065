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
import com.ruoyi.business.domain.BizGuaranteeFee;
import com.ruoyi.business.service.IBizGuaranteeFeeService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;

/**
 * 担保业务台账Controller
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/business/guarantee")
public class BizGuaranteeFeeController extends BaseController
{
    @Autowired
    private IBizGuaranteeFeeService bizGuaranteeFeeService;

    /**
     * 查询担保业务台账列表
     */
    @PreAuthorize("@ss.hasPermi('business:guarantee:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizGuaranteeFee bizGuaranteeFee)
    {
        startPage();
        List<BizGuaranteeFee> list = bizGuaranteeFeeService.selectBizGuaranteeFeeList(bizGuaranteeFee);
        return getDataTable(list);
    }

    /**
     * 导出担保业务台账列表
     */
    @PreAuthorize("@ss.hasPermi('business:guarantee:export')")
    @Log(title = "担保业务台账", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizGuaranteeFee bizGuaranteeFee)
    {
        List<BizGuaranteeFee> list = bizGuaranteeFeeService.selectBizGuaranteeFeeList(bizGuaranteeFee);
        ExcelUtil<BizGuaranteeFee> util = new ExcelUtil<BizGuaranteeFee>(BizGuaranteeFee.class);
        util.exportExcel(response, list, "担保业务台账数据");
    }

    /**
     * 获取担保业务台账详细信息
     */
    @PreAuthorize("@ss.hasPermi('business:guarantee:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(bizGuaranteeFeeService.selectBizGuaranteeFeeById(id));
    }

    /**
     * 新增担保业务台账
     */
    @PreAuthorize("@ss.hasPermi('business:guarantee:add')")
    @Log(title = "担保业务台账", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@Validated @RequestBody BizGuaranteeFee bizGuaranteeFee)
    {
        bizGuaranteeFee.setCreateBy(getUsername());
        return toAjax(bizGuaranteeFeeService.insertBizGuaranteeFee(bizGuaranteeFee));
    }

    /**
     * 修改担保业务台账
     */
    @PreAuthorize("@ss.hasPermi('business:guarantee:edit')")
    @Log(title = "担保业务台账", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@Validated @RequestBody BizGuaranteeFee bizGuaranteeFee)
    {
        bizGuaranteeFee.setUpdateBy(getUsername());
        return toAjax(bizGuaranteeFeeService.updateBizGuaranteeFee(bizGuaranteeFee));
    }

    /**
     * 删除担保业务台账
     */
    @PreAuthorize("@ss.hasPermi('business:guarantee:remove')")
    @Log(title = "担保业务台账", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(bizGuaranteeFeeService.deleteBizGuaranteeFeeByIds(ids));
    }
}
