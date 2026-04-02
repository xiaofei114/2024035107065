package com.ruoyi.business.mapper;

import java.util.List;
import com.ruoyi.business.domain.BizReconciliationLog;

/**
 * 对账日志表Mapper接口
 * 
 * @author ruoyi
 */
public interface BizReconciliationLogMapper
{
    /**
     * 查询对账日志表
     * 
     * @param id 对账日志表主键
     * @return 对账日志表
     */
    public BizReconciliationLog selectBizReconciliationLogById(Long id);

    /**
     * 查询对账日志表列表
     * 
     * @param bizReconciliationLog 对账日志表
     * @return 对账日志表集合
     */
    public List<BizReconciliationLog> selectBizReconciliationLogList(BizReconciliationLog bizReconciliationLog);

    /**
     * 新增对账日志表
     * 
     * @param bizReconciliationLog 对账日志表
     * @return 结果
     */
    public int insertBizReconciliationLog(BizReconciliationLog bizReconciliationLog);

    /**
     * 修改对账日志表
     * 
     * @param bizReconciliationLog 对账日志表
     * @return 结果
     */
    public int updateBizReconciliationLog(BizReconciliationLog bizReconciliationLog);

    /**
     * 删除对账日志表
     * 
     * @param id 对账日志表主键
     * @return 结果
     */
    public int deleteBizReconciliationLogById(Long id);

    /**
     * 批量删除对账日志表
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizReconciliationLogByIds(Long[] ids);
    
    /**
     * 根据对账关联ID查询日志
     * 
     * @param reconciliationId 对账关联ID
     * @return 日志列表
     */
    public List<BizReconciliationLog> selectByReconciliationId(Long reconciliationId);
}
