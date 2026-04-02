package com.ruoyi.business.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.business.domain.BizBankFlow;
import com.ruoyi.business.mapper.BizBankFlowMapper;
import com.ruoyi.business.service.IBizBankFlowService;

/**
 * 账单台账Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class BizBankFlowServiceImpl implements IBizBankFlowService
{
    @Autowired
    private BizBankFlowMapper bizBankFlowMapper;

    /**
     * 查询账单台账
     * 
     * @param id 账单台账主键
     * @return 账单台账
     */
    @Override
    public BizBankFlow selectBizBankFlowById(Long id)
    {
        return bizBankFlowMapper.selectBizBankFlowById(id);
    }

    /**
     * 查询账单台账列表
     * 
     * @param bizBankFlow 账单台账
     * @return 账单台账
     */
    @Override
    public List<BizBankFlow> selectBizBankFlowList(BizBankFlow bizBankFlow)
    {
        return bizBankFlowMapper.selectBizBankFlowList(bizBankFlow);
    }

    /**
     * 新增账单台账
     * 
     * @param bizBankFlow 账单台账
     * @return 结果
     */
    @Override
    public int insertBizBankFlow(BizBankFlow bizBankFlow)
    {
        return bizBankFlowMapper.insertBizBankFlow(bizBankFlow);
    }

    /**
     * 修改账单台账
     * 
     * @param bizBankFlow 账单台账
     * @return 结果
     */
    @Override
    public int updateBizBankFlow(BizBankFlow bizBankFlow)
    {
        return bizBankFlowMapper.updateBizBankFlow(bizBankFlow);
    }

    /**
     * 批量删除账单台账
     * 
     * @param ids 需要删除的账单台账主键
     * @return 结果
     */
    @Override
    public int deleteBizBankFlowByIds(Long[] ids)
    {
        return bizBankFlowMapper.deleteBizBankFlowByIds(ids);
    }

    /**
     * 删除账单台账信息
     * 
     * @param id 账单台账主键
     * @return 结果
     */
    @Override
    public int deleteBizBankFlowById(Long id)
    {
        return bizBankFlowMapper.deleteBizBankFlowById(id);
    }

    /**
     * 查询未被绑定的银行流水列表
     *
     * @param bizBankFlow 账单台账查询条件
     * @return 未被绑定的银行流水集合
     */
    @Override
    public List<BizBankFlow> selectUnboundBankFlowList(BizBankFlow bizBankFlow)
    {
        return bizBankFlowMapper.selectUnboundBankFlowList(bizBankFlow);
    }
}
