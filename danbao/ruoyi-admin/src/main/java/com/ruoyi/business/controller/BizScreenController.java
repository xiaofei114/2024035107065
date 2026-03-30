package com.ruoyi.business.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.business.mapper.BizGuaranteeFeeMapper;
import com.ruoyi.business.mapper.BizBankFlowMapper;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 业务大屏统计接口
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/business/screen")
public class BizScreenController
{
    @Autowired
    private BizGuaranteeFeeMapper bizGuaranteeFeeMapper;
    
    @Autowired
    private BizBankFlowMapper bizBankFlowMapper;

    /**
     * 获取总体统计数据
     * 
     * @return 统计数据
     */
    @GetMapping("/stats")
    public AjaxResult getStats()
    {
        Map<String, Object> stats = new HashMap<>();
        
        // 担保业务总数
        Long totalBusiness = bizGuaranteeFeeMapper.countTotalBusiness();
        stats.put("totalBusiness", totalBusiness != null ? totalBusiness : 0);
        
        // 本月新增担保业务数
        Long monthlyNew = bizGuaranteeFeeMapper.countMonthlyNewBusiness();
        stats.put("monthlyNew", monthlyNew != null ? monthlyNew : 0);
        
        // 担保费总额
        Double totalFee = bizGuaranteeFeeMapper.sumTotalGuaranteeFee();
        stats.put("totalFee", totalFee != null ? totalFee : 0);
        
        // 银行流水总数
        Long totalBankFlow = bizBankFlowMapper.countTotalBankFlow();
        stats.put("totalBankFlow", totalBankFlow != null ? totalBankFlow : 0);
        
        // 收入总额
        Double totalIncome = bizBankFlowMapper.sumTotalIncome();
        stats.put("totalIncome", totalIncome != null ? totalIncome : 0);
        
        // 支出总额
        Double totalExpense = bizBankFlowMapper.sumTotalExpense();
        stats.put("totalExpense", totalExpense != null ? totalExpense : 0);
        
        return AjaxResult.success(stats);
    }

    /**
     * 按银行分组的担保业务数量
     * 
     * @return 银行业务数量统计
     */
    @GetMapping("/guarantee/bybank")
    public AjaxResult getBusinessByBank()
    {
        List<Map<String, Object>> data = bizGuaranteeFeeMapper.countBusinessByBank();
        return AjaxResult.success(data);
    }

    /**
     * 月度担保费趋势
     * 
     * @return 月度担保费统计
     */
    @GetMapping("/guarantee/monthly")
    public AjaxResult getMonthlyGuaranteeFee()
    {
        List<Map<String, Object>> data = bizGuaranteeFeeMapper.sumMonthlyGuaranteeFee();
        return AjaxResult.success(data);
    }

    /**
     * 月度收支数据
     * 
     * @return 月度收支统计
     */
    @GetMapping("/bankflow/monthly")
    public AjaxResult getMonthlyIncomeExpense()
    {
        List<Map<String, Object>> data = bizBankFlowMapper.sumMonthlyIncomeExpense();
        return AjaxResult.success(data);
    }
}
