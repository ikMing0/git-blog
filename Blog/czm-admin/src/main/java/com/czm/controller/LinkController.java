package com.czm.controller;

import com.czm.domain.ResponseResult;
import com.czm.domain.entity.Link;
import com.czm.service.LinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/content/link")
public class LinkController {

    @Autowired
    private LinkService linkService;

    @GetMapping("/list")
    public ResponseResult pageList(Long pageNum,Long pageSize,String name,String status){
        //根据分页查询
        return linkService.pageList(pageNum,pageSize,name,status);
    }

    @PostMapping
    public ResponseResult addLink(@RequestBody Link link){
        //添加友联
        linkService.save(link);
        return ResponseResult.okResult();
    }

    @GetMapping("{id}")
    public ResponseResult getOneById(@PathVariable Long id){
        //获取一条友链的内容
        return ResponseResult.okResult(linkService.getById(id));
    }

    @PutMapping
    public ResponseResult updateLinkById(@RequestBody Link link){
        //根据id更改友联信息
        return linkService.updateLinkById(link);
    }

    @DeleteMapping("{id}")
    public ResponseResult deleteById(@PathVariable Long id){
        linkService.removeById(id);
        return ResponseResult.okResult();
    }
}
