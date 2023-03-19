package com.czm.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czm.constants.SystemConstants;
import com.czm.domain.ResponseResult;
import com.czm.domain.entity.Article;
import com.czm.domain.entity.Category;
import com.czm.domain.vo.CategoryVo;
import com.czm.domain.vo.PageVo;
import com.czm.mapper.CategoryMapper;
import com.czm.service.ArticleService;
import com.czm.service.CategoryService;
import com.czm.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;
import java.util.WeakHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 分类表(Category)表服务实现类
 *
 * @author makejava
 * @since 2022-11-20 21:53:26
 */
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    ArticleService articleService;

    @Override
    public ResponseResult getCategoryList() {
        //全用单表查询
        //查询文章表，状态为已发布
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        List<Article> articles = articleService.list(queryWrapper);
        //获取文章的分类ID，并且去重
        List<Long> categoryIds = articles.stream()
                .distinct()
                .map(article -> article.getCategoryId())
                .collect(Collectors.toList());
        //查询分类表
        List<Category> categoryList = listByIds(categoryIds).stream()
                .filter(category -> SystemConstants.STATUS_NORMAL.equals(category.getStatus()))
                .collect(Collectors.toList());
        //封装vo
        List<CategoryVo> categoryVos = BeanCopyUtils.copyBeanList(categoryList, CategoryVo.class);
        return ResponseResult.okResult(categoryVos);
    }

    @Override
    public ResponseResult listAllCategory() {
        //查询所有分类,状态为0
        LambdaUpdateWrapper<Category> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Category::getStatus,SystemConstants.STATUS_NORMAL);
        List<Category> categories = list(wrapper);
        //分装Vo
        List<CategoryVo> categoryVos = BeanCopyUtils.copyBeanList(categories, CategoryVo.class);
        return ResponseResult.okResult(categoryVos);
    }

    @Override
    public ResponseResult pageList(Long pageNum, Long pageSize, String name, String status) {
        //根据条件分页查询分类列表
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name),Category::getName,name);
        wrapper.eq(StringUtils.hasText(status),Category::getStatus,status);
        Page<Category> page = new Page<>(pageNum,pageSize);
        page(page,wrapper);
        return ResponseResult.okResult(new PageVo(page.getRecords(),page.getTotal()));
    }

    @Override
    public ResponseResult updateCategoryById(Category category) {
        LambdaUpdateWrapper<Category> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Category::getId,category.getId());
        update(category,wrapper);
        return ResponseResult.okResult();
    }


}
