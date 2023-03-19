package com.czm.service.impl;

import com.czm.domain.entity.LoginUser;
import com.czm.utils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ps")
public class PermissionService {

    public boolean hasPermission(String permission){
        //如果是超级管理员,就直接返回true
        if (SecurityUtils.isAdmin()){
            return true;
        }
        //获取用户的权限,判断是否存在该权限
        return SecurityUtils.getLoginUser().getPerms().contains(permission);
    }
}
