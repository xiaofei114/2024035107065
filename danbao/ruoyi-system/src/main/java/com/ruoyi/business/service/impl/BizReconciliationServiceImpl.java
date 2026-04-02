package com.ruoyi.business.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.business.domain.BizBankFlow;
import com.ruoyi.business.domain.BizGuaranteeFee;
import com.ruoyi.business.domain.BizReconciliation;
import com.ruoyi.business.domain.BizReconciliationLog;
import com.ruoyi.business.mapper.BizBankFlowMapper;
import com.ruoyi.business.mapper.BizGuaranteeFeeMapper;
import com.ruoyi.business.mapper.BizReconciliationLogMapper;
import com.ruoyi.business.mapper.BizReconciliationMapper;
import com.ruoyi.business.service.IBizReconciliationService;

/**
 * 对账关联表Service实现
 * 
 * @author ruoyi
 */
@Service
public class BizReconciliationServiceImpl implements IBizReconciliationService
{
    @Autowired
    private BizReconciliationMapper bizReconciliationMapper;
    
    @Autowired
    private BizReconciliationLogMapper bizReconciliationLogMapper;
    
    @Autowired
    private BizGuaranteeFeeMapper bizGuaranteeFeeMapper;
    
    @Autowired
    private BizBankFlowMapper bizBankFlowMapper;

    @Override
    public BizReconciliation selectBizReconciliationById(Long id)
    {
        BizReconciliation reconciliation = bizReconciliationMapper.selectBizReconciliationById(id);
        if (reconciliation != null) {
            // 加载关联的担保业务信息
            BizGuaranteeFee guaranteeFee = bizGuaranteeFeeMapper.selectBizGuaranteeFeeById(reconciliation.getGuaranteeBusinessId());
            reconciliation.setGuaranteeFee(guaranteeFee);
            // 加载关联的银行流水信息
            BizBankFlow bankFlow = bizBankFlowMapper.selectBizBankFlowById(reconciliation.getBankTransactionId());
            reconciliation.setBankFlow(bankFlow);
        }
        return reconciliation;
    }

    @Override
    public List<BizReconciliation> selectBizReconciliationList(BizReconciliation bizReconciliation)
    {
        List<BizReconciliation> list = new ArrayList<>();
        
        // 查询已有的对账记录（包括待人工确认、自动匹配、人工确认等状态）
        List<BizReconciliation> existingList = bizReconciliationMapper.selectBizReconciliationList(bizReconciliation);
        // 加载关联信息
        for (BizReconciliation reconciliation : existingList) {
            // 加载担保业务信息
            if (reconciliation.getGuaranteeBusinessId() != null) {
                BizGuaranteeFee guaranteeFee = bizGuaranteeFeeMapper.selectBizGuaranteeFeeById(reconciliation.getGuaranteeBusinessId());
                if (guaranteeFee != null) {
                    reconciliation.setGuaranteeFee(guaranteeFee);
                }
            }
            // 加载银行流水信息
            if (reconciliation.getBankTransactionId() != null && reconciliation.getBankTransactionId() > 0) {
                BizBankFlow bankFlow = bizBankFlowMapper.selectBizBankFlowById(reconciliation.getBankTransactionId());
                if (bankFlow != null) {
                    reconciliation.setBankFlow(bankFlow);
                }
            }
        }
        list.addAll(existingList);
        
        // 如果没有搜索条件，或者搜索条件是"未对账"，添加未对账的担保业务
        if (bizReconciliation.getReconciliationStatus() == null || "未对账".equals(bizReconciliation.getReconciliationStatus())) {
            // 查询未对账的担保业务
            BizGuaranteeFee guaranteeFeeQuery = new BizGuaranteeFee();
            // 应用搜索条件到担保业务查询
            if (bizReconciliation.getParams() != null) {
                if (bizReconciliation.getParams().get("debtorName") != null) {
                    guaranteeFeeQuery.setDebtorName((String) bizReconciliation.getParams().get("debtorName"));
                }
                if (bizReconciliation.getParams().get("bankName") != null) {
                    guaranteeFeeQuery.setBankName((String) bizReconciliation.getParams().get("bankName"));
                }
            }
            List<BizGuaranteeFee> guaranteeFees = bizGuaranteeFeeMapper.selectBizGuaranteeFeeList(guaranteeFeeQuery);
            for (BizGuaranteeFee guaranteeFee : guaranteeFees) {
                // 检查是否已经对账
                BizReconciliation existingReconciliation = bizReconciliationMapper.selectByGuaranteeBusinessId(guaranteeFee.getId());
                if (existingReconciliation == null) {
                    // 创建未对账的记录
                    BizReconciliation reconciliation = new BizReconciliation();
                    reconciliation.setGuaranteeBusinessId(guaranteeFee.getId());
                    reconciliation.setReconciliationStatus("未对账");
                    reconciliation.setGuaranteeFee(guaranteeFee);
                    list.add(reconciliation);
                }
            }
        }
        
        return list;
    }

    @Override
    public int insertBizReconciliation(BizReconciliation bizReconciliation)
    {
        int result = bizReconciliationMapper.insertBizReconciliation(bizReconciliation);
        if (result > 0) {
            // 记录操作日志
            createReconciliationLog(bizReconciliation, "创建绑定", null, bizReconciliation.getReconciliationStatus());
        }
        return result;
    }

    @Override
    public int updateBizReconciliation(BizReconciliation bizReconciliation)
    {
        BizReconciliation oldReconciliation = selectBizReconciliationById(bizReconciliation.getId());
        int result = bizReconciliationMapper.updateBizReconciliation(bizReconciliation);
        if (result > 0) {
            // 记录操作日志
            createReconciliationLog(bizReconciliation, "状态变更", oldReconciliation.getReconciliationStatus(), bizReconciliation.getReconciliationStatus());
        }
        return result;
    }

    @Override
    public int deleteBizReconciliationById(Long id) {
        // 直接删除对账记录
        int result = bizReconciliationMapper.deleteBizReconciliationById(id);
        return result;
    }

    @Override
    public int deleteBizReconciliationByIds(Long[] ids)
    {
        // 先为每个记录创建操作日志
        for (Long id : ids) {
            BizReconciliation reconciliation = selectBizReconciliationById(id);
            if (reconciliation != null) {
                createReconciliationLog(reconciliation, "解除绑定", reconciliation.getReconciliationStatus(), "未对账");
            }
        }
        // 再批量删除对账记录
        return bizReconciliationMapper.deleteBizReconciliationByIds(ids);
    }

    @Override
    public Map<String, Object> batchAutoReconciliation() {
        Map<String, Object> result = new HashMap<>();
        int autoMatched = 0;
        int needManualConfirm = 0;
        
        // 查询所有未对账的担保业务
        BizGuaranteeFee guaranteeFeeQuery = new BizGuaranteeFee();
        List<BizGuaranteeFee> guaranteeFees = bizGuaranteeFeeMapper.selectBizGuaranteeFeeList(guaranteeFeeQuery);
        
        for (BizGuaranteeFee guaranteeFee : guaranteeFees) {
            // 检查是否已经对账
            BizReconciliation existingReconciliation = bizReconciliationMapper.selectByGuaranteeBusinessId(guaranteeFee.getId());
            if (existingReconciliation != null) {
                continue;
            }
            
            // 查询匹配的银行流水（按银行名称匹配）
            BizBankFlow bankFlowQuery = new BizBankFlow();
            bankFlowQuery.setBankName(guaranteeFee.getBankName());
            List<BizBankFlow> matchedFlows = bizBankFlowMapper.selectBizBankFlowList(bankFlowQuery);
            
            // 进一步匹配：债务人名称和对方户名、保费金额和交易金额
            List<BizBankFlow> fullyMatchedFlows = new java.util.ArrayList<>();
            for (BizBankFlow flow : matchedFlows) {
                // 检查流水是否已被其他业务绑定
                BizReconciliation flowReconciliation = bizReconciliationMapper.selectByBankTransactionId(flow.getId());
                if (flowReconciliation != null) {
                    continue;
                }
                
                // 匹配规则1：债务人名称和对方户名匹配（模糊匹配）
                boolean nameMatch = false;
                if (guaranteeFee.getDebtorName() != null && flow.getOppositeName() != null) {
                    String debtorName = guaranteeFee.getDebtorName().trim();
                    String oppositeName = flow.getOppositeName().trim();
                    // 债务人名称包含对方户名，或对方户名包含债务人名称
                    nameMatch = debtorName.contains(oppositeName) || oppositeName.contains(debtorName);
                }
                
                // 匹配规则2：交易金额 >= 担保费金额
                boolean amountMatch = false;
                if (flow.getTradeAmount() != null && guaranteeFee.getGuaranteeFee() != null) {
                    amountMatch = flow.getTradeAmount().doubleValue() >= guaranteeFee.getGuaranteeFee().doubleValue();
                }
                
                // 两个条件都满足才算完全匹配
                if (nameMatch && amountMatch) {
                    fullyMatchedFlows.add(flow);
                }
            }
            
            if (fullyMatchedFlows.isEmpty()) {
                // 无匹配流水，创建待人工确认记录，绑定虚拟流水ID
                BizReconciliation reconciliation = new BizReconciliation();
                reconciliation.setGuaranteeBusinessId(guaranteeFee.getId());
                reconciliation.setBankTransactionId(0L); // 虚拟流水ID，表示无匹配
                reconciliation.setReconciliationType("自动对账");
                reconciliation.setReconciliationStatus("待人工确认");
                reconciliation.setOperator("系统");
                reconciliation.setOperateTime(new Date());
                insertBizReconciliation(reconciliation);
                needManualConfirm++;
            } else if (fullyMatchedFlows.size() == 1) {
                // 一对一匹配，自动建立绑定
                BizBankFlow matchedFlow = fullyMatchedFlows.get(0);
                BizReconciliation reconciliation = new BizReconciliation();
                reconciliation.setGuaranteeBusinessId(guaranteeFee.getId());
                reconciliation.setBankTransactionId(matchedFlow.getId());
                reconciliation.setReconciliationType("自动对账");
                reconciliation.setReconciliationStatus("自动匹配");
                reconciliation.setOperator("系统");
                reconciliation.setOperateTime(new Date());
                insertBizReconciliation(reconciliation);
                autoMatched++;
            } else {
                // 一对多匹配，创建待人工确认记录
                BizReconciliation reconciliation = new BizReconciliation();
                reconciliation.setGuaranteeBusinessId(guaranteeFee.getId());
                // 绑定第一个匹配的流水
                reconciliation.setBankTransactionId(fullyMatchedFlows.get(0).getId());
                reconciliation.setReconciliationType("自动对账");
                reconciliation.setReconciliationStatus("待人工确认");
                reconciliation.setOperator("系统");
                reconciliation.setOperateTime(new Date());
                insertBizReconciliation(reconciliation);
                needManualConfirm++;
            }
        }
        
        result.put("autoMatched", autoMatched);
        result.put("needManualConfirm", needManualConfirm);
        result.put("totalProcessed", autoMatched + needManualConfirm);
        return result;
    }

    @Override
    public int manualReconciliation(Long guaranteeBusinessId, Long bankTransactionId, String operator, String operatorIp) {
        System.out.println("manualReconciliation开始执行：guaranteeBusinessId=" + guaranteeBusinessId + ", bankTransactionId=" + bankTransactionId);
        
        // 检查担保业务是否存在
        BizGuaranteeFee guaranteeFee = bizGuaranteeFeeMapper.selectBizGuaranteeFeeById(guaranteeBusinessId);
        if (guaranteeFee == null) {
            System.out.println("担保业务不存在：guaranteeBusinessId=" + guaranteeBusinessId);
            return 0;
        }
        System.out.println("担保业务存在：" + guaranteeFee.getProductName());
        
        // 检查银行流水是否存在
        BizBankFlow bankFlow = bizBankFlowMapper.selectBizBankFlowById(bankTransactionId);
        if (bankFlow == null) {
            System.out.println("银行流水不存在：bankTransactionId=" + bankTransactionId);
            return 0;
        }
        System.out.println("银行流水存在：" + bankFlow.getOppositeName());
        
        // 检查银行流水金额是否大于等于担保费
        if (bankFlow.getTradeAmount().doubleValue() < guaranteeFee.getGuaranteeFee().doubleValue()) {
            System.out.println("银行流水金额不足：流水金额=" + bankFlow.getTradeAmount() + ", 担保费=" + guaranteeFee.getGuaranteeFee());
            return 0;
        }
        
        // 检查担保业务是否已经对账
        BizReconciliation existingReconciliation = bizReconciliationMapper.selectByGuaranteeBusinessId(guaranteeBusinessId);
        if (existingReconciliation != null) {
            System.out.println("已存在对账记录：status=" + existingReconciliation.getReconciliationStatus());
            if (!"待人工确认".equals(existingReconciliation.getReconciliationStatus())) {
                // 如果已经存在对账记录且状态不是"待人工确认"，则返回失败
                System.out.println("对账记录状态不是待人工确认，无法手动对账");
                return 0;
            }
        } else {
            System.out.println("未找到现有对账记录，将创建新记录");
        }
        
        // 检查银行流水是否已经被绑定
        BizReconciliation flowReconciliation = bizReconciliationMapper.selectByBankTransactionId(bankTransactionId);
        if (flowReconciliation != null) {
            System.out.println("银行流水已被绑定：guaranteeBusinessId=" + flowReconciliation.getGuaranteeBusinessId() + ", 当前业务id=" + guaranteeBusinessId);
            // 如果银行流水已被其他业务绑定，且不是当前业务，则返回失败
            if (!flowReconciliation.getGuaranteeBusinessId().equals(guaranteeBusinessId)) {
                System.out.println("银行流水已被其他业务绑定，无法使用");
                return 0;
            }
            System.out.println("银行流水已被当前业务绑定，可以继续操作");
        } else {
            System.out.println("银行流水未被绑定，可以使用");
        }
        
        // 如果当前业务已有待人工确认记录，且绑定了虚拟流水（id=0），需要先删除原记录
        if (existingReconciliation != null && "待人工确认".equals(existingReconciliation.getReconciliationStatus())) {
            Long existingBankTransactionId = existingReconciliation.getBankTransactionId();
            if (existingBankTransactionId != null && existingBankTransactionId.equals(0L)) {
                System.out.println("当前业务绑定了虚拟流水，删除原记录");
                // 删除原记录
                bizReconciliationMapper.deleteBizReconciliationById(existingReconciliation.getId());
                // 重置existingReconciliation为null，以便创建新记录
                existingReconciliation = null;
            }
        }
        
        int result;
        if (existingReconciliation != null && "待人工确认".equals(existingReconciliation.getReconciliationStatus())) {
            // 更新已有的待人工确认记录
            existingReconciliation.setBankTransactionId(bankTransactionId);
            existingReconciliation.setReconciliationType("手动对账");
            existingReconciliation.setReconciliationStatus("人工确认");
            existingReconciliation.setOperator(operator != null ? operator : "未知用户");
            existingReconciliation.setOperateTime(new Date());
            result = updateBizReconciliation(existingReconciliation);
            if (result > 0) {
                // 记录操作日志
                createReconciliationLog(existingReconciliation, "手动对账", "待人工确认", "人工确认", operatorIp);
            }
        } else {
            // 创建新的对账关联
            BizReconciliation reconciliation = new BizReconciliation();
            reconciliation.setGuaranteeBusinessId(guaranteeBusinessId);
            reconciliation.setBankTransactionId(bankTransactionId);
            reconciliation.setReconciliationType("手动对账");
            reconciliation.setReconciliationStatus("人工确认");
            reconciliation.setOperator(operator != null ? operator : "未知用户");
            reconciliation.setOperateTime(new Date());
            
            result = insertBizReconciliation(reconciliation);
            if (result > 0) {
                // 记录操作日志
                createReconciliationLog(reconciliation, "手动对账", null, "人工确认", operatorIp);
            }
        }
        return result;
    }

    @Override
    public int unbindReconciliation(Long id, String operator, String operatorIp) {
        BizReconciliation reconciliation = selectBizReconciliationById(id);
        if (reconciliation == null) {
            return 0;
        }
        
        // 先记录操作日志
        createReconciliationLog(reconciliation, "解绑操作", reconciliation.getReconciliationStatus(), "未对账", operatorIp);
        
        // 再删除对账记录
        int result = deleteBizReconciliationById(id);
        return result;
    }

    @Override
    public Map<String, Integer> countReconciliationStatus() {
        Map<String, Integer> statusCount = new HashMap<>();
        List<Map<String, Object>> counts = bizReconciliationMapper.countByStatus();
        
        for (Map<String, Object> count : counts) {
            String status = (String) count.get("status");
            Integer countValue = ((Number) count.get("count")).intValue();
            statusCount.put(status, countValue);
        }
        
        // 确保所有状态都有值
        String[] statuses = {"未对账", "自动匹配", "待人工确认", "人工确认"};
        for (String status : statuses) {
            if (!statusCount.containsKey(status)) {
                statusCount.put(status, 0);
            }
        }
        
        return statusCount;
    }

    /**
     * 创建对账操作日志
     */
    private void createReconciliationLog(BizReconciliation reconciliation, String operationType, String originalStatus, String newStatus) {
        createReconciliationLog(reconciliation, operationType, originalStatus, newStatus, null);
    }

    /**
     * 创建对账操作日志（带IP）
     */
    private void createReconciliationLog(BizReconciliation reconciliation, String operationType, String originalStatus, String newStatus, String operatorIp) {
        BizReconciliationLog log = new BizReconciliationLog();
        log.setReconciliationId(reconciliation.getId());
        log.setOperationType(operationType);
        log.setOriginalStatus(originalStatus);
        log.setNewStatus(newStatus);
        log.setOperator(reconciliation.getOperator());
        log.setOperateTime(new Date());
        log.setOperatorIp(operatorIp != null ? operatorIp : "系统");
        bizReconciliationLogMapper.insertBizReconciliationLog(log);
    }
}
