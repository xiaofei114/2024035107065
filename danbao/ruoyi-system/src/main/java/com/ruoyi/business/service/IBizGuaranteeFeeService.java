package com.ruoyi.business.service;

import java.util.List;
import com.ruoyi.business.domain.BizGuaranteeFee;

/**
 * 担保业务台账Service接口
 * 
 * @author ruoyi
 */
public interface IBizGuaranteeFeeService
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
     * 批量删除担保业务台账
     * 
     * @param ids 需要删除的担保业务台账主键集合
     * @return 结果
     */
    public int deleteBizGuaranteeFeeByIds(Long[] ids);

    /**
     * 删除担保业务台账信息
     * 
     * @param id 担保业务台账主键
     * @return 结果
     */
    public int deleteBizGuaranteeFeeById(Long id);
}
