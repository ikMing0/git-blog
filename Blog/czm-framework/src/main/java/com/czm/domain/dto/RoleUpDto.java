package com.czm.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 更改角色信息接收的Dto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleUpDto {

    private Long id;
    //角色名称
    private String roleName;
    //角色权限字符串
    private String roleKey;
    //显示顺序
    private Integer roleSort;
    //角色状态（0正常 1停用）
    private String status;
    //菜单ids
    private List<Long> menuIds;

}
