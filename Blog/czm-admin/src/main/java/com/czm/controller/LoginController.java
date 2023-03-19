package com.czm.controller;

import com.czm.domain.ResponseResult;
import com.czm.domain.entity.Menu;
import com.czm.domain.entity.User;
import com.czm.domain.vo.AdminUserInfoVo;
import com.czm.domain.vo.RoutersVo;
import com.czm.domain.vo.UserInfoVo;
import com.czm.enums.AppHttpCodeEnum;
import com.czm.exception.SystemException;
import com.czm.service.LoginService;
import com.czm.service.MenuService;
import com.czm.service.RoleService;
import com.czm.utils.BeanCopyUtils;
import com.czm.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/user/login")
    public ResponseResult login(@RequestBody User user){
        if(!StringUtils.hasText(user.getUserName())){
            //提示 必须要传用户名
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        return loginService.login(user);
    }

    @Autowired
    private MenuService menuService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/getInfo")
    public ResponseResult<AdminUserInfoVo> getInfo(){
        //获取用户id
        Long userId = SecurityUtils.getUserId();
        //根据id查询用户menu
        List<String> permissions = menuService.selectPermsByUserId(userId);
        //根据id查询用户role
        List<String> roles = roleService.selectRoleKeyByUserId(userId);
        //封装返回
        UserInfoVo user = BeanCopyUtils.copyBen(SecurityUtils.getLoginUser().getUser(), UserInfoVo.class);
        AdminUserInfoVo adminUserInfoVo = new AdminUserInfoVo(permissions, roles, user);
        return ResponseResult.okResult(adminUserInfoVo);
    }

    @GetMapping("/getRouters")
    public ResponseResult<RoutersVo> getRouters(){
        Long userId = SecurityUtils.getUserId();
        //查询menu，结果是tree的形式
        List<Menu> menus = menuService.selectRouterMenuTreeByUserId(userId);
        return ResponseResult.okResult(new RoutersVo(menus));
    }

    @PostMapping("/user/logout")
    public ResponseResult logout(){
        return loginService.logout();
    }
}
