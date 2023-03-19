package com.czm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.czm.constants.SystemConstants;
import com.czm.domain.entity.LoginUser;
import com.czm.domain.entity.User;
import com.czm.mapper.MenuMapper;
import com.czm.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MenuMapper menuMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//       根据用户名查询用户信息
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,username);
        User user = userMapper.selectOne(queryWrapper);
//        判断是否查到用户，如果没查到抛出异常
        if (Objects.isNull(user)){
            throw new RuntimeException("查询不到用户");
        }
//        查到用户返回用户信息

        //判断是不是后端类型,如果是则需要封装权限信息
        if (user.getType().equals(SystemConstants.ADMAIN)){
            //封装权限信息进LoginUser
            List<String> perms = menuMapper.selectPermsByUserId(user.getId());
            System.out.println(perms);
            return new LoginUser(user,perms);
        }
        return new LoginUser(user,null);
    }
}
