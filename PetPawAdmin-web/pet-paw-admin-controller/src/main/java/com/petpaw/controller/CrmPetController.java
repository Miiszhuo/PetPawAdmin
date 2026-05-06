package com.petpaw.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.petpaw.common.model.PageRequest;
import com.petpaw.common.result.PageResult;
import com.petpaw.common.result.Result;
import com.petpaw.entity.CrmPet;
import com.petpaw.service.CrmPetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * CRM宠物管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/crm/pets")
@RequiredArgsConstructor
public class CrmPetController {

    private final CrmPetService petService;

    /**
     * 分页查询宠物
     */
    @GetMapping
    public Result listPets(PageRequest pageRequest,
                          @RequestParam(required = false) Long customerId,
                          @RequestParam(required = false) String petName,
                          @RequestParam(required = false) String petType,
                          @RequestParam(required = false) String breed,
                          @RequestParam(required = false) String ownerName,
                          @RequestParam(required = false) String status) {
        IPage<CrmPet> page = petService.listPets(pageRequest, customerId, petName, petType, breed, ownerName, status);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
    }

    /**
     * 根据ID获取宠物
     */
    @GetMapping("/{id}")
    public Result getPetById(@PathVariable Long id) {
        CrmPet pet = petService.getById(id);
        return pet != null ? Result.success(pet) : Result.error("宠物不存在");
    }

    /**
     * 创建宠物
     */
    @PostMapping
    public Result createPet(@RequestBody CrmPet pet) {
        boolean success = petService.savePet(pet);
        return success ? Result.success("宠物创建成功") : Result.error("宠物创建失败");
    }

    /**
     * 更新宠物
     */
    @PutMapping("/{id}")
    public Result updatePet(@PathVariable Long id, @RequestBody CrmPet pet) {
        pet.setId(id);
        boolean success = petService.updatePet(pet);
        return success ? Result.success("宠物更新成功") : Result.error("宠物更新失败");
    }

    /**
     * 删除宠物
     */
    @DeleteMapping("/{id}")
    public Result deletePet(@PathVariable Long id) {
        boolean success = petService.deletePet(id);
        return success ? Result.success("宠物删除成功") : Result.error("宠物删除失败");
    }
}
