package com.ruoyi.business.service;

import java.util.List;
import java.util.Map;
import com.ruoyi.business.domain.BizReconciliation;

/**
 * 对账关联表Service接口
 * 
 * @author ruoyi
 */
public interface IBizReconciliationService
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
     * 批量自动对账
     * 
     * @return 结果
     */
    public Map<String, Object> batchAutoReconciliation();
    
    /**
     * 手动对账
     * 
     * @param guaranteeBusinessId 担保业务ID
     * @param bankTransactionId 银行流水ID
     * @param operator 操作人
     * @param operatorIp 操作IP
     * @return 结果
     */
    public int manualReconciliation(Long guaranteeBusinessId, Long bankTransactionId, String operator, String operatorIp);
    
    /**
     * 解绑对账
     * 
     * @param id 对账关联ID
     * @param operator 操作人
     * @param operatorIp 操作IP
     * @return 结果
     */
    public int unbindReconciliation(Long id, String operator, String operatorIp);
    
    /**
     * 统计对账状态
     * 
     * @return 统计结果
     */
    public Map<String, Integer> countReconciliationStatus();
}
