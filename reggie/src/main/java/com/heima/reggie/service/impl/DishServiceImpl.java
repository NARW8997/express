package com.heima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.reggie.dto.DishDto;
import com.heima.reggie.entity.Dish;
import com.heima.reggie.entity.DishFlavor;
import com.heima.reggie.mapper.DishMapper;
import com.heima.reggie.service.DishFlavorService;
import com.heima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * two tables related, so need transaction
     * @param dishDto dish with flavors
     */
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        // first, save the dish info into dish table
        this.save(dishDto);

        // then save the dish flavor
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }
        // save dish flavor
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public DishDto getDtoById(Long id) {
        // get dish first
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        // copy properties
        BeanUtils.copyProperties(dish, dishDto);

        // get flavors
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> flavorList = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(flavorList);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        // update dish first
        this.updateById(dishDto);

        // remove flavor
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        // adding new flavor
        List<DishFlavor> flavorList = dishDto.getFlavors();
        // add dish id to dishFlavor
        for (DishFlavor dishFlavor : flavorList) {
            dishFlavor.setDishId(dishDto.getId());
        }
        dishFlavorService.saveBatch(flavorList);
    }
}
