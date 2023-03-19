package com.czm.controller;

import com.baomidou.mybatisplus.annotation.TableField;
import com.czm.domain.ResponseResult;
import com.czm.domain.dto.ArticleDto;
import com.czm.domain.vo.AdminArticleTagVo;
import com.czm.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @PostMapping
    public ResponseResult addArticle(@RequestBody ArticleDto articleDto){
        //添加博文
        System.out.println(articleDto);
        return articleService.addArticle(articleDto);
    }

    @GetMapping("/list")
    public ResponseResult pageList(Integer pageNum,Integer pageSize,String title,String summary){
        return articleService.pageList(pageNum,pageSize,title,summary);
    }

    @GetMapping("{id}")
    public ResponseResult getOneArticleById(@PathVariable Long id){
        return  articleService.getOneArticleById(id);
    }

    @PutMapping
    public ResponseResult updateArticleById(@RequestBody AdminArticleTagVo adminArticleTagVo){
        return articleService.updateArticleById(adminArticleTagVo);
    }
    @DeleteMapping("{id}")
    public ResponseResult delete(@PathVariable Long id){
        articleService.removeById(id);
        return ResponseResult.okResult();
    }

}
