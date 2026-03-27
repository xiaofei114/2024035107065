package com.ruoyi.business.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.business.domain.BizGuaranteeFee;
import com.ruoyi.business.mapper.BizGuaranteeFeeMapper;
import com.ruoyi.business.service.IBizGuaranteeFeeService;

/**
 * 担保业务台账Service业务层处理
 * 
 * @author ruoyi
 */
@Service
public class BizGuaranteeFeeServiceImpl implements IBizGuaranteeFeeService
{
    @Autowired
    private BizGuaranteeFeeMapper bizGuaranteeFeeMapper;

    /**
     * 查询担保业务台账
     * 
     * @param id 担保业务台账主键
     * @return 担保业务台账
     */
    @Override
    public BizGuaranteeFee selectBizGuaranteeFeeById(Long id)
    {
        return bizGuaranteeFeeMapper.selectBizGuaranteeFeeById(id);
    }

    /**
     * 查询担保业务台账列表
     * 
     * @param bizGuaranteeFee 担保业务台账
     * @return 担保业务台账
     */
    @Override
    public List<BizGuaranteeFee> selectBizGuaranteeFeeList(BizGuaranteeFee bizGuaranteeFee)
    {
        return bizGuaranteeFeeMapper.selectBizGuaranteeFeeList(bizGuaranteeFee);
    }

    /**
     * 新增担保业务台账
     * 
     * @param bizGuaranteeFee 担保业务台账
     * @return 结果
     */
    @Override
    public int insertBizGuaranteeFee(BizGuaranteeFee bizGuaranteeFee)
    {
        // 自动计算担保费
        calculateGuaranteeFee(bizGuaranteeFee);
        return bizGuaranteeFeeMapper.insertBizGuaranteeFee(bizGuaranteeFee);
    }

    /**
     * 修改担保业务台账
     * 
     * @param bizGuaranteeFee 担保业务台账
     * @return 结果
     */
    @Override
    public int updateBizGuaranteeFee(BizGuaranteeFee bizGuaranteeFee)
    {
        // 自动计算担保费
        calculateGuaranteeFee(bizGuaranteeFee);
        return bizGuaranteeFeeMapper.updateBizGuaranteeFee(bizGuaranteeFee);
    }

    /**
     * 批量删除担保业务台账
     * 
     * @param ids 需要删除的担保业务台账主键
     * @return 结果
     */
    @Override
    public int deleteBizGuaranteeFeeByIds(Long[] ids)
    {
        return bizGuaranteeFeeMapper.deleteBizGuaranteeFeeByIds(ids);
    }

    /**
     * 删除担保业务台账信息
     * 
     * @param id 担保业务台账主键
     * @return 结果
     */
    @Override
    public int deleteBizGuaranteeFeeById(Long id)
    {
        return bizGuaranteeFeeMapper.deleteBizGuaranteeFeeById(id);
    }

    /**
     * 计算担保费
     * 公式：担保费 = 主债权金额 × 担保费费率 × 天数 / 365
     * 
     * @param bizGuaranteeFee 担保业务台账
     */
    private void calculateGuaranteeFee(BizGuaranteeFee bizGuaranteeFee)
    {
        if (bizGuaranteeFee.getCreditAmount() != null 
                && bizGuaranteeFee.getGuaranteeFeeRate() != null
                && bizGuaranteeFee.getCreditStartDate() != null 
                && bizGuaranteeFee.getCreditEndDate() != null)
        {
            // 计算天数
            long days = calculateDays(bizGuaranteeFee.getCreditStartDate(), bizGuaranteeFee.getCreditEndDate());
            
            if (days > 0)
            {
                // 担保费 = 主债权金额 × 担保费费率 × 天数 / 365
                BigDecimal amount = bizGuaranteeFee.getCreditAmount();
                BigDecimal rate = bizGuaranteeFee.getGuaranteeFeeRate();
                BigDecimal daysDecimal = new BigDecimal(days);
                BigDecimal yearDays = new BigDecimal(365);
                
                BigDecimal fee = amount.multiply(rate).multiply(daysDecimal).divide(yearDays, 2, RoundingMode.HALF_UP);
                bizGuaranteeFee.setGuaranteeFee(fee);
            }
        }
    }

    /**
     * 计算两个日期之间的天数
     * 
     * @param startDate 起始日期
     * @param endDate 结束日期
     * @return 天数
     */
    private long calculateDays(Date startDate, Date endDate)
    {
        long diffInMillies = endDate.getTime() - startDate.getTime();
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}
