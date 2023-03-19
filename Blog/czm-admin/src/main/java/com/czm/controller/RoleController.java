package com.czm.controller;

import com.czm.domain.ResponseResult;
import com.czm.domain.dto.RoleChangeStatusDto;
import com.czm.domain.dto.RoleUpDto;
import com.czm.domain.entity.Role;
import com.czm.domain.vo.AddRoleDto;
import com.czm.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("system/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public ResponseResult listRole(Long pageNum,Long pageSize,String roleName,String status){
        return roleService.listRole(pageNum,pageSize,roleName,status);
    }

    @PutMapping("/changeStatus")
    public ResponseResult changeStatus(@RequestBody RoleChangeStatusDto role){
        return roleService.changeStatus(role);
    }

    @PostMapping
    public ResponseResult addRole(@RequestBody AddRoleDto addRoleDto){
        return roleService.addRole(addRoleDto);
    }

    @GetMapping("{id}")
    public ResponseResult getOneRole(@PathVariable Long id){
        Role role = roleService.getById(id);
        return ResponseResult.okResult(role);
    }

    @PutMapping
    public ResponseResult updateRole(@RequestBody RoleUpDto roleUpDto){
        return roleService.updateRole(roleUpDto);
    }

    @DeleteMapping("{id}")
    public ResponseResult deleteById(@PathVariable Long id){
        roleService.removeById(id);
        return ResponseResult.okResult();
    }

    @GetMapping("/listAllRole")
    public ResponseResult listAllRole(){
        //查询所有正常的role
        return roleService.listAllRole();
    }
}
