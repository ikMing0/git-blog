package com.czm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.czm.domain.ResponseResult;
import com.czm.domain.entity.LoginUser;
import com.czm.domain.entity.User;
import com.czm.service.LoginService;
import com.czm.utils.JwtUtil;
import com.czm.utils.RedisCache;
import com.czm.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class LoginServiceImpl implements LoginService {



    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;
    @Override
    public ResponseResult login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(),user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
//        判断是否认证通过
        if (Objects.isNull(authenticate)){
            throw new RuntimeException("用户名或密码错误");
        }
        //获取userid生成token
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        Long userId = loginUser.getUser().getId();
        String jwt = JwtUtil.createJWT(userId.toString());
//        把用户信息存入redis
        redisCache.setCacheObject("login:"+userId,loginUser);
        Map token = new HashMap();
        token.put("token",jwt);
        return ResponseResult.okResult(token);
    }

    @Override
    public ResponseResult logout() {
        //获取当前用户id
        Long userId = SecurityUtils.getUserId();
        //删除redis中的token
        redisCache.deleteObject("login:"+userId);
        return ResponseResult.okResult();
    }


}
