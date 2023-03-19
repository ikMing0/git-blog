package com.czm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czm.constants.SystemConstants;
import com.czm.domain.ResponseResult;
import com.czm.domain.dto.RoleChangeStatusDto;
import com.czm.domain.dto.RoleUpDto;
import com.czm.domain.entity.Menu;
import com.czm.domain.entity.Role;
import com.czm.domain.entity.RoleMenu;
import com.czm.domain.vo.AddRoleDto;
import com.czm.domain.vo.MenuTreeVo;
import com.czm.domain.vo.PageVo;
import com.czm.domain.vo.RoleListVo;
import com.czm.mapper.MenuMapper;
import com.czm.mapper.RoleMapper;
import com.czm.service.MenuService;
import com.czm.service.RoleMenuService;
import com.czm.service.RoleService;
import com.czm.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 角色信息表(Role)表服务实现类
 *
 * @author makejava
 * @since 2022-12-04 22:53:07
 */
@Service("roleService")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private MenuService menuService;

    @Autowired
    private RoleMenuService roleMenuService;

    @Override
    public List<String> selectRoleKeyByUserId(Long userId) {
        //判断如果id为1其为管理员
        if(userId == 1L){
            List<String> roleKeys = new ArrayList<>();
            roleKeys.add("admin");
            return roleKeys;
        }
        //查询对应role
        return getBaseMapper().selectRoleKeyByUserId(userId);
    }

    @Override
    public ResponseResult listRole(Long pageNum, Long pageSize, String roleName, String status) {
        //根据条件分页查询角色role
        //条件
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(roleName),Role::getRoleName,roleName);
        wrapper.eq(StringUtils.hasText(status),Role::getStatus,status);
        //分页
        Page<Role> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page(page,wrapper);
        List<Role> roles = page.getRecords();
        List<RoleListVo> roleListVos = BeanCopyUtils.copyBeanList(roles, RoleListVo.class);

        return ResponseResult.okResult(new PageVo(roleListVos,page.getTotal()));
    }

    @Override
    public ResponseResult changeStatus(RoleChangeStatusDto role) {
        //更改角色role状态
        LambdaUpdateWrapper<Role> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Role::getId,role.getRoleId());
        wrapper.set(Role::getStatus,role.getStatus());
        update(wrapper);
        return ResponseResult.okResult();
    }

    @Override
    @Transactional
    public ResponseResult addRole(AddRoleDto addRoleDto) {
        //添加角色
        Role role = BeanCopyUtils.copyBen(addRoleDto, Role.class);
        save(role);
        //把角色所具有的menu绑定在关联表中
        List<RoleMenu> collect = addRoleDto.getMenuIds().stream()
                .map(menuId -> new RoleMenu(role.getId(), menuId))
                .collect(Collectors.toList());
        roleMenuService.saveBatch(collect);
        return ResponseResult.okResult();
    }

    @Override
    @Transactional
    public ResponseResult updateRole(RoleUpDto roleUpDto) {
        //更改角色信息
        Role role = BeanCopyUtils.copyBen(roleUpDto, Role.class);
        LambdaUpdateWrapper<Role> rWrapper = new LambdaUpdateWrapper<>();
        rWrapper.eq(Role::getId,role.getId());
        update(role,rWrapper);
        //更改角色菜单关联表
        LambdaQueryWrapper<RoleMenu> rwWrapper = new LambdaQueryWrapper<>();
        rwWrapper.eq(RoleMenu::getRoleId,role.getId());
        roleMenuService.remove(rwWrapper);
        List<RoleMenu> roleMenus = roleUpDto.getMenuIds().stream()
                .map(aLong -> new RoleMenu(role.getId(),aLong))
                .collect(Collectors.toList());
        roleMenuService.saveBatch(roleMenus);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult listAllRole() {
        //查询所有状态为正常的角色
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getStatus, SystemConstants.ROLE_STATUS_NORMAL);
        List<Role> roleList = list(wrapper);
        return ResponseResult.okResult(roleList);
    }


}
