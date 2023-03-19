package com.czm.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.czm.domain.ResponseResult;
import com.czm.domain.entity.Menu;
import com.czm.domain.vo.OneMenuVo;
import com.czm.service.MenuService;
import com.czm.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("system/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping("/list")
    public ResponseResult menuList(String status, String menuName){
        return menuService.MenuList(status,menuName);
    }

    @PostMapping
    public ResponseResult menuAdd(@RequestBody Menu menu){
        menuService.save(menu);
        return ResponseResult.okResult();
    }
    @GetMapping("/{id}")
    public ResponseResult getMenu(@PathVariable Long id){
        LambdaUpdateWrapper<Menu> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Menu::getId,id);
        Menu menu = menuService.getOne(wrapper);
        OneMenuVo oneMenuVo = BeanCopyUtils.copyBen(menu, OneMenuVo.class);
        return ResponseResult.okResult(oneMenuVo);
    }

    @PutMapping
    public ResponseResult putMenu(@RequestBody Menu menu){
        return menuService.putMenu(menu);
    }

    @DeleteMapping("{menuId}")
    public ResponseResult deleteMenu(@PathVariable Long menuId){
        return menuService.deleteMenu(menuId);
    }

    @GetMapping("/treeselect")
    public ResponseResult treeSelect(){
        return menuService.treeSelect();
    }

    @GetMapping("/roleMenuTreeselect/{id}")
    public ResponseResult roleMenuTreeSelect(@PathVariable Long id){
        return menuService.roleMenuTreeSelect(id);
    }
}
