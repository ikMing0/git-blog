package com.czm.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OneMenuVo {
    //菜单图标
    private String icon;
    private Long id;
    //菜单名称
    private String menuName;
    //菜单类型（M目录 C菜单 F按钮）
    private String menuType;
    //显示顺序
    private Integer orderNum;
    //父菜单ID
    private Long parentId;
    //路由地址
    private String path;
    //备注
    private String remark;
    //菜单状态（0显示 1隐藏）
    private String visible;

}
