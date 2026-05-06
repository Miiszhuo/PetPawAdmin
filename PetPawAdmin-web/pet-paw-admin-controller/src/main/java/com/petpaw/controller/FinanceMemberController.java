package com.petpaw.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.petpaw.common.model.PageRequest;
import com.petpaw.common.result.PageResult;
import com.petpaw.common.result.Result;
import com.petpaw.entity.FinanceMemberCard;
import com.petpaw.entity.FinanceRechargeRecord;
import com.petpaw.service.FinanceMemberCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 会员管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/finance/members")
@RequiredArgsConstructor
public class FinanceMemberController {

    private final FinanceMemberCardService memberCardService;

    /**
     * 分页查询会员卡
     */
    @GetMapping("/cards")
    public Result listMemberCards(PageRequest pageRequest,
                                 @RequestParam(required = false) Long customerId,
                                 @RequestParam(required = false) String cardType,
                                 @RequestParam(required = false) Integer status) {
        IPage<FinanceMemberCard> page = memberCardService.listMemberCards(pageRequest, customerId, cardType, status);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
    }

    /**
     * 根据ID获取会员卡
     */
    @GetMapping("/cards/{id}")
    public Result getMemberCardById(@PathVariable Long id) {
        FinanceMemberCard memberCard = memberCardService.getById(id);
        return memberCard != null ? Result.success(memberCard) : Result.error("会员卡不存在");
    }

    /**
     * 根据客户ID获取会员卡
     */
    @GetMapping("/customers/{customerId}/card")
    public Result getMemberCardByCustomerId(@PathVariable Long customerId) {
        FinanceMemberCard memberCard = memberCardService.getMemberCardByCustomerId(customerId);
        return Result.success(memberCard);
    }

    /**
     * 创建会员卡
     */
    @PostMapping("/cards")
    public Result createMemberCard(@RequestBody FinanceMemberCard memberCard) {
        boolean success = memberCardService.createMemberCard(memberCard);
        return success ? Result.success("会员卡创建成功") : Result.error("会员卡创建失败");
    }

    /**
     * 更新会员卡
     */
    @PutMapping("/cards/{id}")
    public Result updateMemberCard(@PathVariable Long id, @RequestBody FinanceMemberCard memberCard) {
        memberCard.setId(id);
        boolean success = memberCardService.updateById(memberCard);
        return success ? Result.success("会员卡更新成功") : Result.error("会员卡更新失败");
    }

    /**
     * 会员卡充值
     */
    @PostMapping("/cards/{id}/recharge")
    public Result recharge(@PathVariable Long id,
                          @RequestParam BigDecimal amount,
                          @RequestParam String paymentMethod,
                          @RequestParam Long cashierId) {
        boolean success = memberCardService.recharge(id, amount, paymentMethod, cashierId);
        return success ? Result.success("充值成功") : Result.error("充值失败");
    }

    /**
     * 会员卡消费
     */
    @PostMapping("/cards/{id}/consume")
    public Result consume(@PathVariable Long id,
                         @RequestParam BigDecimal amount,
                         @RequestParam(required = false) Long orderId,
                         @RequestParam Long cashierId) {
        boolean success = memberCardService.consume(id, amount, orderId, cashierId);
        return success ? Result.success("消费成功") : Result.error("消费失败");
    }

    /**
     * 会员卡积分变动
     */
    @PostMapping("/cards/{id}/points")
    public Result updatePoints(@PathVariable Long id,
                              @RequestParam Integer points,
                              @RequestParam String reason) {
        boolean success = memberCardService.updatePoints(id, points, reason);
        return success ? Result.success("积分更新成功") : Result.error("积分更新失败");
    }

    /**
     * 会员卡挂失
     */
    @PostMapping("/cards/{id}/report-lost")
    public Result reportLost(@PathVariable Long id, @RequestParam String reason) {
        boolean success = memberCardService.reportLost(id, reason);
        return success ? Result.success("挂失成功") : Result.error("挂失失败");
    }

    /**
     * 会员卡激活
     */
    @PostMapping("/cards/{id}/activate")
    public Result activate(@PathVariable Long id) {
        boolean success = memberCardService.activate(id);
        return success ? Result.success("激活成功") : Result.error("激活失败");
    }

    /**
     * 会员卡注销
     */
    @PostMapping("/cards/{id}/deactivate")
    public Result deactivate(@PathVariable Long id, @RequestParam String reason) {
        boolean success = memberCardService.deactivate(id, reason);
        return success ? Result.success("注销成功") : Result.error("注销失败");
    }

    /**
     * 获取充值记录
     */
    @GetMapping("/cards/{id}/recharge-records")
    public Result getRechargeRecords(@PathVariable Long id) {
        List<FinanceRechargeRecord> records = memberCardService.getRechargeRecords(id);
        return Result.success(records);
    }

    /**
     * 获取即将到期的会员卡
     */
    @GetMapping("/cards/expiring")
    public Result getExpiringCards(@RequestParam(defaultValue = "30") Integer days) {
        List<FinanceMemberCard> cards = memberCardService.getExpiringCards(days);
        return Result.success(cards);
    }

    /**
     * 删除会员卡
     */
    @DeleteMapping("/cards/{id}")
    public Result deleteMemberCard(@PathVariable Long id) {
        boolean success = memberCardService.removeById(id);
        return success ? Result.success("会员卡删除成功") : Result.error("会员卡删除失败");
    }
}
