package com.czm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.czm.domain.ResponseResult;
import com.czm.domain.dto.ArticleDto;
import com.czm.domain.entity.Article;
import com.czm.domain.vo.AdminArticleTagVo;

public interface ArticleService extends IService<Article> {
    ResponseResult hotArticleList();

    ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId);

    ResponseResult getArticleDetail(Long id);

    ResponseResult updateViewCount(Long id);

    ResponseResult addArticle(ArticleDto articleDto);

    ResponseResult pageList(Integer pageNum, Integer pageSize, String title, String summary);

    ResponseResult getOneArticleById(Long id);

    ResponseResult updateArticleById(AdminArticleTagVo adminArticleTagVo);
}
