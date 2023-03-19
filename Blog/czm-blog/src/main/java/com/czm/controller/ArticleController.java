package com.czm.controller;

import com.czm.domain.ResponseResult;
import com.czm.domain.entity.Article;
import com.czm.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

   /* @GetMapping("/list")
    public List<Article> test(){
        return articleService.list();
    }*/

    @GetMapping("/hotArticleList")
    public ResponseResult hotArticleList(){
        //查询热门文章，封装成ResponseResult返回
        return articleService.hotArticleList();
    }


    @GetMapping("/articleList")
    public ResponseResult articleList(Integer pageNum,Integer pageSize,Long categoryId){
        return articleService.articleList(pageNum,pageSize,categoryId);
    }

    @PutMapping("/updateViewCount/{id}")
    public ResponseResult updateViewCount(@PathVariable("id") Long id){
        return articleService.updateViewCount(id);
    }

    @GetMapping("/{id}")
    public ResponseResult getArticleDetail(@PathVariable("id") Long id){
        return articleService.getArticleDetail(id);
    }
}
