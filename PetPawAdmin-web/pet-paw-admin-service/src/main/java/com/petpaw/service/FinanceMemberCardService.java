package com.petpaw.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.petpaw.common.model.PageRequest;
import com.petpaw.entity.FinanceMemberCard;
import com.petpaw.entity.FinanceRechargeRecord;

import java.math.BigDecimal;
import java.util.List;

/**
 * 会员卡服务接口
 */
public interface FinanceMemberCardService extends IService<FinanceMemberCard> {

    /**
     * 分页查询会员卡
     */
    IPage<FinanceMemberCard> listMemberCards(PageRequest pageRequest, Long customerId, String cardType, Integer status);

    /**
     * 根据客户ID获取会员卡
     */
    FinanceMemberCard getMemberCardByCustomerId(Long customerId);

    /**
     * 创建会员卡
     */
    boolean createMemberCard(FinanceMemberCard memberCard);

    /**
     * 会员卡充值
     */
    boolean recharge(Long memberCardId, BigDecimal amount, String paymentMethod, Long cashierId);

    /**
     * 会员卡消费
     */
    boolean consume(Long memberCardId, BigDecimal amount, Long orderId, Long cashierId);

    /**
     * 会员卡积分变动
     */
    boolean updatePoints(Long memberCardId, Integer points, String reason);

    /**
     * 会员卡挂失
     */
    boolean reportLost(Long memberCardId, String reason);

    /**
     * 会员卡激活
     */
    boolean activate(Long memberCardId);

    /**
     * 会员卡注销
     */
    boolean deactivate(Long memberCardId, String reason);

    /**
     * 获取充值记录
     */
    List<FinanceRechargeRecord> getRechargeRecords(Long memberCardId);

    /**
     * 获取即将到期的会员卡
     */
    List<FinanceMemberCard> getExpiringCards(Integer days);
}
