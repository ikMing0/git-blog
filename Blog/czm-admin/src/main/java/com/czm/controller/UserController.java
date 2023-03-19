package com.czm.controller;

import com.czm.domain.ResponseResult;
import com.czm.domain.dto.UserDto;
import com.czm.domain.dto.UserUpDto;
import com.czm.domain.entity.User;
import com.czm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public ResponseResult pageSelect(Long pageNum,Long pageSize,String userName,String phonenumber,String status){
        return userService.pageSelect(pageNum,pageSize,userName,phonenumber,status);
    }

    @PostMapping
    public ResponseResult addUser(@RequestBody UserDto userDto){
        return userService.addUser(userDto);
    }

    @DeleteMapping("{id}")
    public ResponseResult deleteById(@PathVariable Long id){
        userService.removeById(id);
        return ResponseResult.okResult();
    }

    @GetMapping("{id}")
    public  ResponseResult getUserRolesRoleIds(@PathVariable Long id){
        //根据id查询用户信息回显
        return userService.getUserRolesRoleIds(id);
    }

    @PutMapping
    public ResponseResult updateUserById(@RequestBody UserUpDto userUpDto){
        //更改用户信息
        return userService.updateUserById(userUpDto);
    }
}
