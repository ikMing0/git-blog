package com.czm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czm.constants.SystemConstants;
import com.czm.domain.ResponseResult;
import com.czm.domain.dto.ArticleDto;
import com.czm.domain.entity.Article;
import com.czm.domain.entity.ArticleTag;
import com.czm.domain.entity.Category;
import com.czm.domain.entity.Tag;
import com.czm.domain.vo.*;
import com.czm.mapper.ArticleMapper;
import com.czm.mapper.ArticleTagMapper;
import com.czm.mapper.TagMapper;
import com.czm.service.ArticleService;
import com.czm.service.ArticleTagService;
import com.czm.service.CategoryService;
import com.czm.utils.BeanCopyUtils;
import com.czm.utils.RedisCache;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    CategoryService categoryService;
    @Autowired
    RedisCache redisCache;

    @Override
    public ResponseResult hotArticleList() {
        //查询热门文章，封装成ResponseResult返回
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        //必须是正式文章
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        //最多只查询十条
        Page<Article> page = new Page<>(1,10);
        //按照浏览量继续排序
        queryWrapper.orderByDesc(Article::getViewCount);
        page(page,queryWrapper);
        //bean拷贝
        List<Article> articles = page.getRecords();

        //在redis中获取viewCount
        List<Article> articleList = articles.stream()
                .map(article -> { Integer viewCount = redisCache.getCacheMapValue("viewCount", article.getId().toString());
                    return article.setViewCount(viewCount.longValue());
                })
                .collect(Collectors.toList());


        List<HotArticleVo> vs = BeanCopyUtils.copyBeanList(articleList, HotArticleVo.class);
        return ResponseResult.okResult(vs);
    }

    @Override
    public ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId) {
        //查询条件
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        //如果categoryId就要求查询时要和传入的相同
        queryWrapper.eq(Objects.nonNull(categoryId) && categoryId>0,Article::getCategoryId,categoryId);
        //状态是正式发布的
        queryWrapper.eq(Article::getStatus,SystemConstants.ARTICLE_STATUS_NORMAL);
        //对isTop进行降序
        queryWrapper.orderByDesc(Article::getIsTop);
        //分页查询
        Page<Article> page = new Page<>(pageNum,pageSize);
        page(page,queryWrapper);
        List<Article> articles = page.getRecords();
        //查询categoryName
        articles.stream()
                .map(article -> {
                    //获取分类id获取分类信息，获取分类名称
                    //把分类名称设置给article
                    Category category = categoryService.getById(article.getCategoryId());
                    article.setCategoryName(category.getName());
                    return article;
                })
                .collect(Collectors.toList());
        //封装查询结果
        List<ArticleListVo> articleListVos = BeanCopyUtils.copyBeanList(articles, ArticleListVo.class);
        PageVo pageVo = new PageVo(articleListVos, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult getArticleDetail(Long id) {
        //根据id查询文章
        Article article = getById(id);
        Integer viewCount = redisCache.getCacheMapValue("viewCount", id.toString());
        article.setViewCount(viewCount.longValue());
        //封装vo
        ArticleDetailVo articleDetailVo = BeanCopyUtils.copyBen(article, ArticleDetailVo.class);
        //根据categoryId查询categoryName
        Category category = categoryService.getById(articleDetailVo.getCategoryId());
        if (category!=null){
            articleDetailVo.setCategoryName(category.getName());
        }
        //封装返回
        return ResponseResult.okResult(articleDetailVo);
    }

    @Override
    public ResponseResult updateViewCount(Long id) {
        //更新redis中对应id的浏览量
        redisCache.incrementCacheMapValue("viewCount",id.toString(),1);
        return ResponseResult.okResult();
    }

    @Autowired
    private ArticleTagService articleTagService;

    @Override
    @Transactional
    public ResponseResult addArticle(ArticleDto articleDto) {
        //根据条件添加博文
        Article article = BeanCopyUtils.copyBen(articleDto, Article.class);
        save(article);
        //操作多个表，添加事务@Transactional
        //还需把tag(标签)添加到article和tag关联表
        List<ArticleTag> articleTags = articleDto.getTags().stream()
                .map(tagId -> new ArticleTag(article.getId(), tagId))
                .collect(Collectors.toList());
        //添加 博客和标签的关联
        articleTagService.saveBatch(articleTags);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult pageList(Integer pageNum, Integer pageSize, String title, String summary) {
        //根据条件查询后台文章列表
        LambdaUpdateWrapper<Article> wrapper = new LambdaUpdateWrapper<>();
        wrapper.like(StringUtils.hasText(title),Article::getTitle,title);
        wrapper.like(StringUtils.hasText(summary),Article::getSummary,summary);
        //分页查询
        Page<Article> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page(page,wrapper);
        List<Article> articles = page.getRecords();
        //封装返回
        List<AdminArticleListVo> adminArticleListVos = BeanCopyUtils.copyBeanList(articles, AdminArticleListVo.class);
        PageVo pageVo = new PageVo(adminArticleListVos,page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Autowired
    private ArticleTagMapper articleTagMapper;
    @Override
    public ResponseResult getOneArticleById(Long id) {
        //根据id查询文章信息
        Article article = getById(id);

        //根据文章id查询文章的所有标签
        LambdaUpdateWrapper<ArticleTag> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(ArticleTag::getArticleId,article.getId());
        List<ArticleTag> articleTags = articleTagMapper.selectList(wrapper);


        //获取到标签，把他转换为list集合
        List<Long> tagIds = articleTags.stream()
                .map(articleTag -> articleTag.getTagId())
                .collect(Collectors.toList());
        //封装vo
        AdminArticleTagVo adminArticleTagVo = BeanCopyUtils.copyBen(article, AdminArticleTagVo.class);
        //把标签集合赋值给vo
        adminArticleTagVo.setTags(tagIds);
        return ResponseResult.okResult(adminArticleTagVo);
    }

    @Override
    @Transactional
    public ResponseResult updateArticleById(AdminArticleTagVo adminArticleTagVo) {
        //根据id更改article内容
        Article article = BeanCopyUtils.copyBen(adminArticleTagVo, Article.class);
        LambdaUpdateWrapper<Article> wrapper = new LambdaUpdateWrapper<>();
        System.out.println(article.getId());
        wrapper.eq(Article::getId,article.getId());
        update(article,wrapper);
        //先删掉所有和这个article有关的标签
        LambdaUpdateWrapper<ArticleTag> wrapper1 = new LambdaUpdateWrapper<>();
        wrapper1.eq(ArticleTag::getArticleId,article.getId());
        articleTagMapper.delete(wrapper1);
        //再添加标签
        //更改articleTag关联表
        List<ArticleTag> articleTags = adminArticleTagVo.getTags().stream()
                .map(aLong -> {
                    ArticleTag articleTag = new ArticleTag(article.getId(), aLong);
                    articleTagMapper.insert(articleTag);
                    return articleTag;
                })
                .collect(Collectors.toList());

        //articleTagService.saveOrUpdateBatch(articleTags);
        return ResponseResult.okResult();
    }
}
