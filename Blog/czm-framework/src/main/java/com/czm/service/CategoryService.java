package com.czm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.czm.domain.ResponseResult;
import com.czm.domain.entity.Category;

import javax.servlet.http.HttpServletResponse;


/**
 * 分类表(Category)表服务接口
 *
 * @author makejava
 * @since 2022-11-20 21:53:25
 */
public interface CategoryService extends IService<Category> {

    ResponseResult getCategoryList();

    ResponseResult listAllCategory();

    ResponseResult pageList(Long pageNum, Long pageSize, String name, String status);

    ResponseResult updateCategoryById(Category category);
}
