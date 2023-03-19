package com.czm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.czm.domain.ResponseResult;
import com.czm.domain.dto.UserDto;
import com.czm.domain.dto.UserUpDto;
import com.czm.domain.entity.User;


/**
 * 用户表(User)表服务接口
 *
 * @author makejava
 * @since 2022-11-27 15:50:55
 */
public interface UserService extends IService<User> {

    ResponseResult userInfo();

    ResponseResult updateUserInfo(User user);

    ResponseResult register(User user);

    ResponseResult pageSelect(Long pageNum, Long pageSize, String userName, String phonenumber, String status);

    ResponseResult addUser(UserDto userDto);

    ResponseResult getUserRolesRoleIds(Long id);

    ResponseResult updateUserById(UserUpDto userUpDto);
}
