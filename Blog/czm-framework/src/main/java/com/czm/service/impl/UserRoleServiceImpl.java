package com.czm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czm.domain.entity.UserRole;
import com.czm.mapper.UserRoleMapper;
import com.czm.service.UserRoleService;
import org.springframework.stereotype.Service;

/**
 * 用户和角色关联表(UserRole)表服务实现类
 *
 * @author makejava
 * @since 2022-12-18 18:22:16
 */
@Service("userRoleService")
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

}
