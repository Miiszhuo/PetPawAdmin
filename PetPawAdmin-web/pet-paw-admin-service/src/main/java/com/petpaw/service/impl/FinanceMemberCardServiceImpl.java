package com.petpaw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.petpaw.common.exception.BusinessException;
import com.petpaw.common.model.PageRequest;
import com.petpaw.entity.CrmCustomer;
import com.petpaw.entity.FinanceMemberCard;
import com.petpaw.entity.FinanceRechargeRecord;
import com.petpaw.mapper.FinanceMemberCardMapper;
import com.petpaw.mapper.FinanceRechargeRecordMapper;
import com.petpaw.service.CrmCustomerService;
import com.petpaw.service.FinanceMemberCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 会员卡服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FinanceMemberCardServiceImpl extends ServiceImpl<FinanceMemberCardMapper, FinanceMemberCard>
        implements FinanceMemberCardService {

    private final FinanceRechargeRecordMapper rechargeRecordMapper;
    private final CrmCustomerService customerService;

    @Override
    public IPage<FinanceMemberCard> listMemberCards(PageRequest pageRequest, Long customerId, String cardType, Integer status) {
        Page<FinanceMemberCard> page = new Page<>(pageRequest.getCurrent(), pageRequest.getSize());
        LambdaQueryWrapper<FinanceMemberCard> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(customerId != null, FinanceMemberCard::getCustomerId, customerId)
               .eq(StringUtils.hasText(cardType), FinanceMemberCard::getCardType, cardType)
               .eq(status != null, FinanceMemberCard::getStatus, status)
               .eq(FinanceMemberCard::getDeleted, 0)
               .orderByDesc(FinanceMemberCard::getCreateTime);

        IPage<FinanceMemberCard> resultPage = baseMapper.selectPage(page, wrapper);
        
        // 填充客户信息
        if (!resultPage.getRecords().isEmpty()) {
            Set<Long> customerIds = resultPage.getRecords().stream()
                .map(FinanceMemberCard::getCustomerId)
                .collect(Collectors.toSet());
            
            if (!customerIds.isEmpty()) {
                Map<Long, CrmCustomer> customerMap = customerService.listByIds(customerIds).stream()
                    .collect(Collectors.toMap(CrmCustomer::getId, c -> c));
                
                for (FinanceMemberCard card : resultPage.getRecords()) {
                    CrmCustomer customer = customerMap.get(card.getCustomerId());
                    if (customer != null) {
                        // 这里假设 FinanceMemberCard 实体类有非数据库字段用于展示
                        // 或者前端需要从其他接口获取，或者直接透传
                        // 暂时将客户信息放入 card 对象中，如果 card 对象没有对应字段，可能需要 VO
                        // 建议 FinanceMemberCard 增加 @TableField(exist=false) 的 customerName 和 phone
                        card.setCustomerName(customer.getCustomerName());
                        card.setPhone(customer.getPhone());
                    }
                }
            }
        }
        
        return resultPage;
    }

    @Override
    public FinanceMemberCard getMemberCardByCustomerId(Long customerId) {
        LambdaQueryWrapper<FinanceMemberCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceMemberCard::getCustomerId, customerId)
               .eq(FinanceMemberCard::getStatus, 1)
               .eq(FinanceMemberCard::getDeleted, 0);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    @Transactional
    public boolean updateById(FinanceMemberCard entity) {
        boolean success = super.updateById(entity);
        
        // 同步更新客户表的会员类型
        if (success && StringUtils.hasText(entity.getCardType())) {
            Long customerId = entity.getCustomerId();
            // 如果传入的实体没有customerId，则从数据库查询
            if (customerId == null && entity.getId() != null) {
                FinanceMemberCard existing = baseMapper.selectById(entity.getId());
                if (existing != null) {
                    customerId = existing.getCustomerId();
                }
            }
            
            if (customerId != null) {
                CrmCustomer customer = customerService.getById(customerId);
                if (customer != null) {
                    customer.setCustomerType(entity.getCardType());
                    customerService.updateById(customer);
                }
            }
        }
        return success;
    }

    @Override
    @Transactional
    public boolean createMemberCard(FinanceMemberCard memberCard) {
        try {
            // 生成卡号
            if (!StringUtils.hasText(memberCard.getCardNumber())) {
                memberCard.setCardNumber("MC" + System.currentTimeMillis());
            }

            // 设置初始值
            if (memberCard.getBalance() == null) {
                memberCard.setBalance(BigDecimal.ZERO);
            }
            if (memberCard.getTotalRecharge() == null) {
                memberCard.setTotalRecharge(BigDecimal.ZERO);
            }
            if (memberCard.getTotalConsumption() == null) {
                memberCard.setTotalConsumption(BigDecimal.ZERO);
            }
            if (memberCard.getPoints() == null) {
                memberCard.setPoints(0);
            }
            if (memberCard.getStatus() == null) {
                memberCard.setStatus(1); // 默认启用
            }

            memberCard.setCreateTime(LocalDateTime.now());
            memberCard.setUpdateTime(LocalDateTime.now());

            boolean success = save(memberCard);
            
            // 同步更新客户表的会员类型
            if (success && memberCard.getCustomerId() != null) {
                CrmCustomer customer = customerService.getById(memberCard.getCustomerId());
                if (customer != null) {
                    customer.setCustomerType(memberCard.getCardType()); // 假设 cardType 对应 normal/vip/diamond
                    customerService.updateById(customer);
                }
            }
            
            return success;
        } catch (Exception e) {
            log.error("创建会员卡失败", e);
            throw new BusinessException("创建会员卡失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean recharge(Long memberCardId, BigDecimal amount, String paymentMethod, Long cashierId) {
        FinanceMemberCard memberCard = getById(memberCardId);
        if (memberCard == null) {
            throw new BusinessException("会员卡不存在");
        }
        if (memberCard.getStatus() != 1) {
            throw new BusinessException("会员卡未激活");
        }

        try {
            // 更新会员卡余额
            memberCard.setBalance(memberCard.getBalance().add(amount));
            memberCard.setTotalRecharge(memberCard.getTotalRecharge().add(amount));
            memberCard.setUpdateTime(LocalDateTime.now());
            updateById(memberCard);

            // 记录充值记录
            FinanceRechargeRecord record = new FinanceRechargeRecord();
            record.setMemberCardId(memberCardId);
            record.setAmount(amount);
            record.setPaymentMethod(paymentMethod);
            record.setCashierId(cashierId);
            record.setRemark("会员卡充值");
            record.setCreateTime(LocalDateTime.now());
            rechargeRecordMapper.insert(record);

            return true;
        } catch (Exception e) {
            log.error("会员卡充值失败", e);
            throw new BusinessException("会员卡充值失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean consume(Long memberCardId, BigDecimal amount, Long orderId, Long cashierId) {
        FinanceMemberCard memberCard = getById(memberCardId);
        if (memberCard == null) {
            throw new BusinessException("会员卡不存在");
        }
        if (memberCard.getStatus() != 1) {
            throw new BusinessException("会员卡未激活");
        }
        if (memberCard.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("会员卡余额不足");
        }

        try {
            // 更新会员卡余额和消费记录
            memberCard.setBalance(memberCard.getBalance().subtract(amount));
            memberCard.setTotalConsumption(memberCard.getTotalConsumption().add(amount));
            memberCard.setUpdateTime(LocalDateTime.now());
            updateById(memberCard);

            return true;
        } catch (Exception e) {
            log.error("会员卡消费失败", e);
            throw new BusinessException("会员卡消费失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean updatePoints(Long memberCardId, Integer points, String reason) {
        FinanceMemberCard memberCard = getById(memberCardId);
        if (memberCard == null) {
            throw new BusinessException("会员卡不存在");
        }

        try {
            memberCard.setPoints(memberCard.getPoints() + points);
            memberCard.setUpdateTime(LocalDateTime.now());
            return updateById(memberCard);
        } catch (Exception e) {
            log.error("更新会员卡积分失败", e);
            throw new BusinessException("更新会员卡积分失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean reportLost(Long memberCardId, String reason) {
        FinanceMemberCard memberCard = getById(memberCardId);
        if (memberCard == null) {
            throw new BusinessException("会员卡不存在");
        }

        try {
            memberCard.setStatus(0); // 停用
            memberCard.setRemark((memberCard.getRemark() != null ? memberCard.getRemark() : "") +
                                " [挂失原因: " + reason + "]");
            memberCard.setUpdateTime(LocalDateTime.now());
            return updateById(memberCard);
        } catch (Exception e) {
            log.error("会员卡挂失失败", e);
            throw new BusinessException("会员卡挂失失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean activate(Long memberCardId) {
        FinanceMemberCard memberCard = getById(memberCardId);
        if (memberCard == null) {
            throw new BusinessException("会员卡不存在");
        }

        try {
            memberCard.setStatus(1); // 启用
            memberCard.setUpdateTime(LocalDateTime.now());
            return updateById(memberCard);
        } catch (Exception e) {
            log.error("会员卡激活失败", e);
            throw new BusinessException("会员卡激活失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public boolean deactivate(Long memberCardId, String reason) {
        FinanceMemberCard memberCard = getById(memberCardId);
        if (memberCard == null) {
            throw new BusinessException("会员卡不存在");
        }

        try {
            memberCard.setStatus(0); // 停用
            memberCard.setRemark((memberCard.getRemark() != null ? memberCard.getRemark() : "") +
                                " [注销原因: " + reason + "]");
            memberCard.setUpdateTime(LocalDateTime.now());
            return updateById(memberCard);
        } catch (Exception e) {
            log.error("会员卡注销失败", e);
            throw new BusinessException("会员卡注销失败: " + e.getMessage());
        }
    }

    @Override
    public List<FinanceRechargeRecord> getRechargeRecords(Long memberCardId) {
        LambdaQueryWrapper<FinanceRechargeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceRechargeRecord::getMemberCardId, memberCardId)
               .eq(FinanceRechargeRecord::getDeleted, 0)
               .orderByDesc(FinanceRechargeRecord::getCreateTime);

        return rechargeRecordMapper.selectList(wrapper);
    }

    @Override
    public List<FinanceMemberCard> getExpiringCards(Integer days) {
        LocalDate expireDate = LocalDate.now().plusDays(days);

        LambdaQueryWrapper<FinanceMemberCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.le(FinanceMemberCard::getExpireTime, expireDate)
               .eq(FinanceMemberCard::getStatus, 1)
               .eq(FinanceMemberCard::getDeleted, 0)
               .orderByAsc(FinanceMemberCard::getExpireTime);

        return baseMapper.selectList(wrapper);
    }
}
