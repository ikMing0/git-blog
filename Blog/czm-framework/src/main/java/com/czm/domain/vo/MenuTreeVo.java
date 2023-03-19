package com.czm.domain.vo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.czm.domain.entity.Menu;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MenuTreeVo {

    private List<MenuTreeVo> children;
    private Long id;
    //菜单名称
    private String menuName;
    //父菜单ID
    private Long parentId;
    //菜单名称
    private String label;
}
