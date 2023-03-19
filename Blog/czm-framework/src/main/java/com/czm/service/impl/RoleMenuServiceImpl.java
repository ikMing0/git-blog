package com.czm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czm.domain.entity.RoleMenu;
import com.czm.mapper.RoleMenuMapper;
import com.czm.service.RoleMenuService;
import org.springframework.stereotype.Service;

/**
 * 角色和菜单关联表(RoleMenu)表服务实现类
 *
 * @author makejava
 * @since 2022-12-17 17:39:33
 */
@Service("roleMenuService")
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {

}
