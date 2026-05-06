package com.petpaw.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.petpaw.common.model.PageRequest;
import com.petpaw.common.result.PageResult;
import com.petpaw.common.result.Result;
import com.petpaw.entity.CrmCustomer;
import com.petpaw.service.CrmCustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * CRM客户管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/crm/customers")
@RequiredArgsConstructor
public class CrmCustomerController {

    private final CrmCustomerService customerService;

    /**
     * 分页查询客户
     */
    @GetMapping
    public Result listCustomers(PageRequest pageRequest,
                               @RequestParam(required = false) String customerName,
                               @RequestParam(required = false) String phone,
                               @RequestParam(required = false) String customerType,
                               @RequestParam(required = false) String startDate,
                               @RequestParam(required = false) String endDate) {
        IPage<CrmCustomer> page = customerService.listCustomers(pageRequest, customerName, phone, null, customerType, startDate, endDate);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
    }

    /**
     * 根据ID获取客户
     */
    @GetMapping("/{id}")
    public Result getCustomerById(@PathVariable Long id) {
        CrmCustomer customer = customerService.getById(id);
        return customer != null ? Result.success(customer) : Result.error("客户不存在");
    }

    /**
     * 创建客户
     */
    @PostMapping
    public Result createCustomer(@RequestBody CrmCustomer customer) {
        boolean success = customerService.saveCustomer(customer);
        return success ? Result.success("客户创建成功") : Result.error("客户创建失败");
    }

    /**
     * 更新客户
     */
    @PutMapping("/{id}")
    public Result updateCustomer(@PathVariable Long id, @RequestBody CrmCustomer customer) {
        customer.setId(id);
        boolean success = customerService.updateCustomer(customer);
        return success ? Result.success("客户更新成功") : Result.error("客户更新失败");
    }

    /**
     * 删除客户
     */
    @DeleteMapping("/{id}")
    public Result deleteCustomer(@PathVariable Long id) {
        boolean success = customerService.deleteCustomer(id);
        return success ? Result.success("客户删除成功") : Result.error("客户删除失败");
    }
}
