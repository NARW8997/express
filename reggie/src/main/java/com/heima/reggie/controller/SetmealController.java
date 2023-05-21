package com.heima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.reggie.common.R;
import com.heima.reggie.dto.SetmealDto;
import com.heima.reggie.entity.Category;
import com.heima.reggie.entity.Setmeal;
import com.heima.reggie.service.CategoryService;
import com.heima.reggie.service.SetmealDishService;
import com.heima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealService setmealService;

    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        log.info("saving a set meal...");
        log.info(setmealDto.toString());
        setmealService.saveWithDish(setmealDto);
        return R.success("saved a set meal successfully");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null, Setmeal::getName, name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo);

        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Setmeal> pageInfoRecords = pageInfo.getRecords();
        List<SetmealDto> setmealDtos = new ArrayList<>();
        for (Setmeal setmeal : pageInfoRecords) {
            // get category id
            Long categoryId = setmeal.getCategoryId();
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal, setmealDto);

            Category category = categoryService.getById(categoryId);
            if (category != null) {
                setmealDto.setCategoryName(category.getName());
            }

            setmealDtos.add(setmealDto);
        }

        dtoPage.setRecords(setmealDtos);

        return R.success(dtoPage);
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids) {
        log.info("Deleting setmeal(s)...");
        log.info(ids.toString());

        setmealService.removeWithDish(ids);
        return R.success("Deleted setmeal(s) successfully");
    }

}
