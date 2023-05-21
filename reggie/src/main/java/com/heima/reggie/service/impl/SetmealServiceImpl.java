package com.heima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.reggie.common.CustomException;
import com.heima.reggie.dto.SetmealDto;
import com.heima.reggie.entity.Setmeal;
import com.heima.reggie.entity.SetmealDish;
import com.heima.reggie.mapper.SetmealMapper;
import com.heima.reggie.service.SetmealDishService;
import com.heima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        for (SetmealDish dish : setmealDishes) {
            dish.setSetmealId(setmealDto.getId());
        }

        setmealDishService.saveBatch(setmealDishes);
    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> setmealIds) {
        // check if it has setmeals in setmealIds and status == 1
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId, setmealIds);
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus, 1);
        int count = this.count(setmealLambdaQueryWrapper);

        if (count > 0) {
            throw new CustomException("Cannot delete setmeal which is still selling");
        }

        // if no above setmeals, then delete
        this.removeByIds(setmealIds);

        // then delete setmeal dish
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId, setmealIds);

        setmealDishService.remove(setmealDishLambdaQueryWrapper);
    }

}
