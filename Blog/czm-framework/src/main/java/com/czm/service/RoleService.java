package com.czm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.czm.domain.ResponseResult;
import com.czm.domain.dto.RoleChangeStatusDto;
import com.czm.domain.dto.RoleUpDto;
import com.czm.domain.entity.Role;
import com.czm.domain.vo.AddRoleDto;

import java.util.List;


/**
 * 角色信息表(Role)表服务接口
 *
 * @author makejava
 * @since 2022-12-04 22:53:07
 */
public interface RoleService extends IService<Role> {

    List<String> selectRoleKeyByUserId(Long userId);

    ResponseResult listRole(Long pageNum, Long pageSize, String roleName, String status);

    ResponseResult changeStatus(RoleChangeStatusDto role);


    ResponseResult addRole(AddRoleDto addRoleDto);

    ResponseResult updateRole(RoleUpDto roleUpDto);

    ResponseResult listAllRole();

}
