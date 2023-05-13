package com.heima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.reggie.entity.Category;

public interface CategoryService extends IService<Category> {

    /**
     * remove a category, considering if related dish/setmeal exists
     * @param id category id
     */
    void remove(Long id);
}
