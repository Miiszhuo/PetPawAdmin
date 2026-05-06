package com.petpaw.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.petpaw.common.model.PageRequest;
import com.petpaw.common.result.PageResult;
import com.petpaw.common.result.Result;
import com.petpaw.entity.AppointmentServiceItem;
import com.petpaw.service.AppointmentServiceItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 预约服务项目管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/appointment/service-items")
@RequiredArgsConstructor
public class AppointmentServiceItemController {

    private final AppointmentServiceItemService serviceItemService;

    /**
     * 分页查询服务项目
     */
    @GetMapping
    public Result listServiceItems(PageRequest pageRequest,
                                  @RequestParam(required = false) String serviceName,
                                  @RequestParam(required = false) String category,
                                  @RequestParam(required = false) Integer status,
                                  @RequestParam(required = false) String requiredSkill) {
        IPage<AppointmentServiceItem> page = serviceItemService.listServiceItems(pageRequest, serviceName, category, status, requiredSkill);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
    }

    /**
     * 根据ID获取服务项目
     */
    @GetMapping("/{id}")
    public Result getServiceItemById(@PathVariable Long id) {
        AppointmentServiceItem serviceItem = serviceItemService.getById(id);
        return serviceItem != null ? Result.success(serviceItem) : Result.error("服务项目不存在");
    }

    /**
     * 创建服务项目
     */
    @PostMapping
    public Result createServiceItem(@RequestBody AppointmentServiceItem serviceItem) {
        boolean success = serviceItemService.saveServiceItem(serviceItem);
        return success ? Result.success("服务项目创建成功") : Result.error("服务项目创建失败");
    }

    /**
     * 更新服务项目
     */
    @PutMapping("/{id}")
    public Result updateServiceItem(@PathVariable Long id, @RequestBody AppointmentServiceItem serviceItem) {
        serviceItem.setId(id);
        boolean success = serviceItemService.updateServiceItem(serviceItem);
        return success ? Result.success("服务项目更新成功") : Result.error("服务项目更新失败");
    }

    /**
     * 删除服务项目
     */
    @DeleteMapping("/{id}")
    public Result deleteServiceItem(@PathVariable Long id) {
        boolean success = serviceItemService.deleteServiceItem(id);
        return success ? Result.success("服务项目删除成功") : Result.error("服务项目删除失败");
    }
}
