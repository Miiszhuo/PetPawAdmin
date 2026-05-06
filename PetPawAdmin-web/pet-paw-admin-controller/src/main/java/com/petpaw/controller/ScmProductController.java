package com.petpaw.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.petpaw.common.model.PageRequest;
import com.petpaw.common.result.PageResult;
import com.petpaw.common.result.Result;
import com.petpaw.entity.ScmProduct;
import com.petpaw.service.ScmProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * SCM商品管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/scm/products")
@RequiredArgsConstructor
public class ScmProductController {

    private final ScmProductService productService;

    /**
     * 分页查询商品
     */
    @GetMapping
    public Result listProducts(PageRequest pageRequest,
                              @RequestParam(required = false) String productName,
                              @RequestParam(required = false) String category,
                              @RequestParam(required = false) Integer status,
                              @RequestParam(required = false) Long supplierId,
                              @RequestParam(required = false) String stockStatus,
                              @RequestParam(required = false) String brand) {
        IPage<ScmProduct> page = productService.listProducts(pageRequest, productName, category, status, supplierId, stockStatus, brand);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()));
    }

    /**
     * 根据ID获取商品
     */
    @GetMapping("/{id}")
    public Result getProductById(@PathVariable Long id) {
        ScmProduct product = productService.getById(id);
        return product != null ? Result.success(product) : Result.error("商品不存在");
    }

    /**
     * 创建商品
     */
    @PostMapping
    public Result createProduct(@RequestBody ScmProduct product) {
        boolean success = productService.saveProduct(product);
        return success ? Result.success("商品创建成功") : Result.error("商品创建失败");
    }

    /**
     * 更新商品
     */
    @PutMapping("/{id}")
    public Result updateProduct(@PathVariable Long id, @RequestBody ScmProduct product) {
        product.setId(id);
        boolean success = productService.updateProduct(product);
        return success ? Result.success("商品更新成功") : Result.error("商品更新失败");
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public Result deleteProduct(@PathVariable Long id) {
        boolean success = productService.deleteProduct(id);
        return success ? Result.success("商品删除成功") : Result.error("商品删除失败");
    }

    /**
     * 更新库存
     */
    @PutMapping("/{id}/stock")
    public Result updateStock(@PathVariable Long id, @RequestParam Integer quantity) {
        boolean success = productService.updateStock(id, quantity);
        return success ? Result.success("库存更新成功") : Result.error("库存更新失败");
    }
}
