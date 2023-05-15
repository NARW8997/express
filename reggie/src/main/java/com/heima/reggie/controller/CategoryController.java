package com.heima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.reggie.common.R;
import com.heima.reggie.entity.Category;
import com.heima.reggie.service.CategoryService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody Category category) {
        log.info("Adding new category: {}", category);
        categoryService.save(category);
        return R.success("Added a new category successfully");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        Page<Category> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);

        categoryService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    @DeleteMapping
    public R<String> remove(Long id) {
        log.info("removing a category...");
        categoryService.remove(id);
        return R.success("Deleting a category successfully");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category) {
        log.info("Updating an category...");
        categoryService.updateById(category);
        return R.success("Updating an category successfully");
    }

    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        // create a lambda query wrapper
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        // condition query
        queryWrapper.eq(category.getType() != null, Category::getType, category.getType());
        // order by sort
        queryWrapper.orderByAsc(Category::getSort);
        // get res
        List<Category> categoryList = categoryService.list(queryWrapper);
        return R.success(categoryList);
    }
}
