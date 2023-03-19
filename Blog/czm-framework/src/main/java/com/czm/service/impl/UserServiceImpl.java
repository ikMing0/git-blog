package com.czm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czm.domain.ResponseResult;
import com.czm.domain.dto.UserDto;
import com.czm.domain.dto.UserUpDto;
import com.czm.domain.entity.Role;
import com.czm.domain.entity.User;
import com.czm.domain.entity.UserRole;
import com.czm.domain.vo.PageVo;
import com.czm.domain.vo.UpdateUserVo;
import com.czm.domain.vo.UserInfoVo;
import com.czm.enums.AppHttpCodeEnum;
import com.czm.exception.SystemException;
import com.czm.mapper.UserMapper;
import com.czm.mapper.UserRoleMapper;
import com.czm.service.RoleService;
import com.czm.service.UserRoleService;
import com.czm.service.UserService;
import com.czm.utils.BeanCopyUtils;
import com.czm.utils.JwtUtil;
import com.czm.utils.SecurityUtils;
import org.ehcache.impl.internal.store.heap.holders.CopiedOnHeapKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 用户表(User)表服务实现类
 *
 * @author makejava
 * @since 2022-11-27 15:50:56
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

    @Override
    public ResponseResult userInfo() {
        //获取userId
        Long userId = SecurityUtils.getUserId();
        //根据userId查询用户信息
        User byId = getById(userId);
        //封装VO返回
        UserInfoVo userInfoVo = BeanCopyUtils.copyBen(byId, UserInfoVo.class);
        return ResponseResult.okResult(userInfoVo);
    }

    @Override
    public ResponseResult updateUserInfo(User user) {
        //获取userId
        Long userId = SecurityUtils.getUserId();
        user.setId(userId);
        //根据id更改userInfo
        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(User::getId,userId);
        update(user,updateWrapper);
        return ResponseResult.okResult();
    }


    @Override
    public ResponseResult register(User user) {
        //对数据进行非空判断
        if(!StringUtils.hasText(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getPassword())){
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getEmail())){
            throw new SystemException(AppHttpCodeEnum.EMAIL_NOT_NULL);
        }
        if(!StringUtils.hasText(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.NICKNAME_NOT_NULL);
        }
        //对数据进行是否存在的判断
        if(userNameExist(user.getUserName())){
            throw new SystemException(AppHttpCodeEnum.USERNAME_EXIST);
        }
        if(nickNameExist(user.getNickName())){
            throw new SystemException(AppHttpCodeEnum.NICKNAME_EXIST);
        }
        //...
        //对密码进行加密
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        //存入数据库
        save(user);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult pageSelect(Long pageNum, Long pageSize, String userName, String phonenumber, String status) {
        //根据条件分页查询
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(userName),User::getUserName,userName);
        wrapper.eq(StringUtils.hasText(phonenumber),User::getPhonenumber,phonenumber);
        wrapper.eq(StringUtils.hasText(status),User::getStatus,status);
        Page page = new Page(pageNum,pageSize);
        page(page);
        return ResponseResult.okResult(new PageVo(page.getRecords(),page.getTotal()));
    }

    @Override
    @Transactional
    public ResponseResult addUser(UserDto userDto) {
        //排查
        //用户名不能为空，否则提示：必需填写用户名
        //用户名必须之前未存在，否则提示：用户名已存在
        //手机号必须之前未存在，否则提示：手机号已存在
        //邮箱必须之前未存在，否则提示：邮箱已存在
        if (!StringUtils.hasText(userDto.getUserName())){
            return ResponseResult.errorResult(500,"用户名不能为空");
        }
        List<User> users = list();
        List<String> userNames = users.stream().map(user -> user.getUserName()).collect(Collectors.toList());
        if (userNames.contains(userDto.getUserName())){
            return ResponseResult.errorResult(500,"用户名已存在");
        }
        if (users.stream().map(user -> user.getEmail()).collect(Collectors.toList()).contains(userDto.getEmail())){
            return ResponseResult.errorResult(500,"邮箱已存在");
        }
        if (users.stream().map(user -> user.getPhonenumber()).collect(Collectors.toList()).contains(userDto.getPhonenumber())){
            return ResponseResult.errorResult(500,"手机号已存在");
        }


        //加密用户密码，添加新用户
        User user = BeanCopyUtils.copyBen(userDto, User.class);
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);
        save(user);
        //添加用户和角色关联表
        List<UserRole> userRoles = userDto.getRoleIds().stream()
                .map(s -> new UserRole(user.getId(), s))
                .collect(Collectors.toList());
        userRoleService.saveBatch(userRoles);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getUserRolesRoleIds(Long id) {
        //获取用户关联角色的id列表
        LambdaQueryWrapper<UserRole> urWrapper = new LambdaQueryWrapper<>();
        urWrapper.eq(UserRole::getUserId,id);
        List<UserRole> userRoles = userRoleService.list(urWrapper);
        List<Long> roleIds = userRoles.stream()
                .map(userRole -> userRole.getRoleId())
                .collect(Collectors.toList());
        //获取所有角色的列表
        List<Role> roles = roleService.list();
        //用户信息
        User user = getById(id);
        return ResponseResult.okResult(new UpdateUserVo(roleIds,roles,user));
    }

    @Override
    @Transactional
    public ResponseResult updateUserById(UserUpDto userUpDto) {
        //根据用户id更改用户信息
        User user = BeanCopyUtils.copyBen(userUpDto, User.class);
        LambdaQueryWrapper<User> uWrapper = new LambdaQueryWrapper<>();
        uWrapper.eq(User::getId,user.getId());
        update(user,uWrapper);
        //更改用户和角色的关联表
        LambdaQueryWrapper<UserRole> urWrapper = new LambdaQueryWrapper<>();
        urWrapper.eq(UserRole::getUserId,user.getId());
        userRoleService.remove(urWrapper);
        List<UserRole> userRoles = userUpDto.getRoleIds().stream()
                .map(l -> new UserRole(userUpDto.getId(), l))
                .collect(Collectors.toList());
        userRoleService.saveBatch(userRoles);
        return ResponseResult.okResult();
    }

    private boolean nickNameExist(String nickName) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getNickName,nickName);
        return count(queryWrapper)>0;
    }

    private boolean userNameExist(String userName) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName,userName);
        return count(queryWrapper)>0;
    }
}
