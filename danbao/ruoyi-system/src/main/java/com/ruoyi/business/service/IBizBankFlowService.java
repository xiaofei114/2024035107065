package com.ruoyi.business.service;

import java.util.List;
import com.ruoyi.business.domain.BizBankFlow;

/**
 * 账单台账Service接口
 * 
 * @author ruoyi
 */
public interface IBizBankFlowService
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
     * 批量删除账单台账
     * 
     * @param ids 需要删除的账单台账主键集合
     * @return 结果
     */
    public int deleteBizBankFlowByIds(Long[] ids);

    /**
     * 删除账单台账信息
     * 
     * @param id 账单台账主键
     * @return 结果
     */
    public int deleteBizBankFlowById(Long id);

    /**
     * 查询未被绑定的银行流水列表
     *
     * @param bizBankFlow 账单台账查询条件
     * @return 未被绑定的银行流水集合
     */
    public List<BizBankFlow> selectUnboundBankFlowList(BizBankFlow bizBankFlow);
}
