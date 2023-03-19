package com.czm.controller;

import com.czm.domain.ResponseResult;
import com.czm.domain.entity.Tag;
import com.czm.domain.vo.PageVo;
import com.czm.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/content/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    @GetMapping("/list")
    public ResponseResult<PageVo> list(Integer pageNum, Integer pageSize , String name, String remark){
        return tagService.pageTagList(pageNum,pageSize,name,remark);
    }

    @PostMapping
    public ResponseResult add(@RequestBody Tag tag){
        return tagService.addTag(tag);
    }

    @DeleteMapping("/{id}")
    public ResponseResult deleteTag(@PathVariable Integer id){
        tagService.removeById(id);
        return ResponseResult.okResult();
    }

    @GetMapping("/{id}")
    public ResponseResult getOneTagById(@PathVariable Integer id){
        return tagService.getOneTagById(id);
    }

    @PutMapping
    public ResponseResult updateTagById(@RequestBody Tag tag){
        return tagService.updateTagById(tag);
    }

    @GetMapping("/listAllTag")
    public ResponseResult listAllTag(){
        //查询所有标签接口
        return tagService.listAllTag();
    }
}
