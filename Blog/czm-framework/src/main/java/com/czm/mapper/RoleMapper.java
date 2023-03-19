package com.czm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.czm.domain.entity.Role;

import java.util.List;


/**
 * 角色信息表(Role)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-04 22:53:07
 */
public interface RoleMapper extends BaseMapper<Role> {

    List<String> selectRoleKeyByUserId(Long userId);
}

