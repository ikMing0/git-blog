package com.czm.service;


import com.czm.domain.ResponseResult;
import com.czm.domain.entity.User;

public interface BlogLoginService {

    ResponseResult login(User user);

    ResponseResult logout();
}
