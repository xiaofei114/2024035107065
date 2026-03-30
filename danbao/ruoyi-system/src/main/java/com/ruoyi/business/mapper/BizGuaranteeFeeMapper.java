package com.ruoyi.business.mapper;

import java.util.List;
import com.ruoyi.business.domain.BizGuaranteeFee;

/**
 * 担保业务台账Mapper接口
 * 
 * @author ruoyi
 */
public interface BizGuaranteeFeeMapper
{
    /**
     * 查询担保业务台账
     * 
     * @param id 担保业务台账主键
     * @return 担保业务台账
     */
    public BizGuaranteeFee selectBizGuaranteeFeeById(Long id);

    /**
     * 查询担保业务台账列表
     * 
     * @param bizGuaranteeFee 担保业务台账
     * @return 担保业务台账集合
     */
    public List<BizGuaranteeFee> selectBizGuaranteeFeeList(BizGuaranteeFee bizGuaranteeFee);

    /**
     * 新增担保业务台账
     * 
     * @param bizGuaranteeFee 担保业务台账
     * @return 结果
     */
    public int insertBizGuaranteeFee(BizGuaranteeFee bizGuaranteeFee);

    /**
     * 修改担保业务台账
     * 
     * @param bizGuaranteeFee 担保业务台账
     * @return 结果
     */
    public int updateBizGuaranteeFee(BizGuaranteeFee bizGuaranteeFee);

    /**
     * 删除担保业务台账
     * 
     * @param id 担保业务台账主键
     * @return 结果
     */
    public int deleteBizGuaranteeFeeById(Long id);

    /**
     * 批量删除担保业务台账
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizGuaranteeFeeByIds(Long[] ids);
    
    /**
     * 统计担保业务总数
     * 
     * @return 业务总数
     */
    public Long countTotalBusiness();
    
    /**
     * 统计本月新增担保业务数
     * 
     * @return 本月新增业务数
     */
    public Long countMonthlyNewBusiness();
    
    /**
     * 统计担保费总额
     * 
     * @return 担保费总额
     */
    public Double sumTotalGuaranteeFee();
    
    /**
     * 按银行分组统计担保业务数量
     * 
     * @return 银行业务数量统计
     */
    public List<java.util.Map<String, Object>> countBusinessByBank();
    
    /**
     * 统计近6个月月度担保费
     * 
     * @return 月度担保费统计
     */
    public List<java.util.Map<String, Object>> sumMonthlyGuaranteeFee();
}
