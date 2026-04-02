package com.ruoyi.business.mapper;

import java.util.List;
import com.ruoyi.business.domain.BizBankFlow;

/**
 * 账单台账Mapper接口
 * 
 * @author ruoyi
 */
public interface BizBankFlowMapper
{
    /**
     * 查询账单台账
     * 
     * @param id 账单台账主键
     * @return 账单台账
     */
    public BizBankFlow selectBizBankFlowById(Long id);

    /**
     * 查询账单台账列表
     * 
     * @param bizBankFlow 账单台账
     * @return 账单台账集合
     */
    public List<BizBankFlow> selectBizBankFlowList(BizBankFlow bizBankFlow);

    /**
     * 新增账单台账
     * 
     * @param bizBankFlow 账单台账
     * @return 结果
     */
    public int insertBizBankFlow(BizBankFlow bizBankFlow);

    /**
     * 修改账单台账
     * 
     * @param bizBankFlow 账单台账
     * @return 结果
     */
    public int updateBizBankFlow(BizBankFlow bizBankFlow);

    /**
     * 删除账单台账
     * 
     * @param id 账单台账主键
     * @return 结果
     */
    public int deleteBizBankFlowById(Long id);

    /**
     * 批量删除账单台账
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizBankFlowByIds(Long[] ids);
    
    /**
     * 统计银行流水总数
     * 
     * @return 流水总数
     */
    public Long countTotalBankFlow();
    
    /**
     * 统计收入总额
     * 
     * @return 收入总额
     */
    public Double sumTotalIncome();
    
    /**
     * 统计支出总额
     * 
     * @return 支出总额
     */
    public Double sumTotalExpense();
    
    /**
     * 统计近6个月月度收支数据
     * 
     * @return 月度收支统计
     */
    public List<java.util.Map<String, Object>> sumMonthlyIncomeExpense();
    
    /**
     * 查询未被绑定的银行流水列表
     * 
     * @param bizBankFlow 账单台账查询条件
     * @return 未被绑定的银行流水集合
     */
    public List<BizBankFlow> selectUnboundBankFlowList(BizBankFlow bizBankFlow);
}
