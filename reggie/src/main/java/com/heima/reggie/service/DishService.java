package com.heima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.reggie.dto.DishDto;
import com.heima.reggie.entity.Dish;

public interface DishService extends IService<Dish> {

    void saveWithFlavor(DishDto dishDto);

    DishDto getDtoById(Long id);

    void updateWithFlavor(DishDto dishDto);
}
