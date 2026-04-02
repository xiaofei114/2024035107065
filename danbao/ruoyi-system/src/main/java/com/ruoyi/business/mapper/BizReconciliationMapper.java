package com.ruoyi.business.mapper;

import java.util.List;
import com.ruoyi.business.domain.BizReconciliation;

/**
 * 对账关联表Mapper接口
 * 
 * @author ruoyi
 */
public interface BizReconciliationMapper
{
    /**
     * 查询对账关联表
     * 
     * @param id 对账关联表主键
     * @return 对账关联表
     */
    public BizReconciliation selectBizReconciliationById(Long id);

    /**
     * 查询对账关联表列表
     * 
     * @param bizReconciliation 对账关联表
     * @return 对账关联表集合
     */
    public List<BizReconciliation> selectBizReconciliationList(BizReconciliation bizReconciliation);

    /**
     * 新增对账关联表
     * 
     * @param bizReconciliation 对账关联表
     * @return 结果
     */
    public int insertBizReconciliation(BizReconciliation bizReconciliation);

    /**
     * 修改对账关联表
     * 
     * @param bizReconciliation 对账关联表
     * @return 结果
     */
    public int updateBizReconciliation(BizReconciliation bizReconciliation);

    /**
     * 删除对账关联表
     * 
     * @param id 对账关联表主键
     * @return 结果
     */
    public int deleteBizReconciliationById(Long id);

    /**
     * 批量删除对账关联表
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizReconciliationByIds(Long[] ids);
    
    /**
     * 根据担保业务ID查询对账记录
     * 
     * @param guaranteeBusinessId 担保业务ID
     * @return 对账记录
     */
    public BizReconciliation selectByGuaranteeBusinessId(Long guaranteeBusinessId);
    
    /**
     * 根据银行流水ID查询对账记录
     * 
     * @param bankTransactionId 银行流水ID
     * @return 对账记录
     */
    public BizReconciliation selectByBankTransactionId(Long bankTransactionId);
    
    /**
     * 统计对账状态
     * 
     * @return 统计结果
     */
    public List<java.util.Map<String, Object>> countByStatus();
}
