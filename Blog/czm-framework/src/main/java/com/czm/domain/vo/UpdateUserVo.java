package com.czm.domain.vo;

import com.czm.domain.entity.Role;
import com.czm.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * admin更新用户所用到的类
 */
public class UpdateUserVo {

    private List<Long> roleIds;

    private List<Role> roles;

    private User user;

}
