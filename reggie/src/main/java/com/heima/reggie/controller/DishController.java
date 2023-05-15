package com.heima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.reggie.common.R;
import com.heima.reggie.dto.DishDto;
import com.heima.reggie.entity.Category;
import com.heima.reggie.entity.Dish;
import com.heima.reggie.service.CategoryService;
import com.heima.reggie.service.DishFlavorService;
import com.heima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto) {
        log.info("creating a dish...");
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);
        return R.success("created a dish successfully");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        Page<DishDto> pageInfoDishDto = new Page<>();

        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(name != null, Dish::getName, name);
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);

        // search page with category id
        dishService.page(pageInfo, lambdaQueryWrapper);

        // copy pageInfo to pageInfoDishDto, without records
        BeanUtils.copyProperties(pageInfo, pageInfoDishDto, "records");

        // set category name
        List<Dish> records = pageInfo.getRecords();

        List<DishDto> recordsDto = new ArrayList<>();

        for (Dish record : records) {
            DishDto dishDto = new DishDto();
            // copy properties
            BeanUtils.copyProperties(record, dishDto);
            // get category id
            Long categoryId = record.getCategoryId();
            // query for category name
            Category category = categoryService.getById(categoryId);
            // set category name
            if (category != null) {
                dishDto.setCategoryName(category.getName());
                recordsDto.add(dishDto);
            }
        }

        // set new records list with category name
        pageInfoDishDto.setRecords(recordsDto);

        return R.success(pageInfoDishDto);
    }

    @GetMapping("/{id}")
    public R<DishDto> getDtoById(@PathVariable Long id) {
        DishDto dishDto = dishService.getDtoById(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        log.info("updating a dish...");
        log.info(dishDto.toString());

        dishService.updateWithFlavor(dishDto);
        return R.success("updated a dish successfully");
    }
}
