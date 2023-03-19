package com.czm.service;

import com.czm.domain.ResponseResult;
import com.czm.domain.entity.User;

public interface LoginService {
    ResponseResult login(User user);

    ResponseResult logout();
}
