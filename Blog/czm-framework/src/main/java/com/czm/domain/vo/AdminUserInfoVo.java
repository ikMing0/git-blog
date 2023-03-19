package com.czm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserInfoVo {

    public List<String> permissions;

    public List<String> roles;

    public UserInfoVo user;
}
