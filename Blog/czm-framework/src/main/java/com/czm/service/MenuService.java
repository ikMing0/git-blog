package com.czm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.czm.domain.ResponseResult;
import com.czm.domain.entity.Menu;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 菜单权限表(Menu)表服务接口
 *
 * @author makejava
 * @since 2022-12-04 22:52:42
 */
public interface MenuService extends IService<Menu> {

    List<String> selectPermsByUserId(Long userId);

    List<Menu> selectRouterMenuTreeByUserId(Long userId);

    ResponseResult MenuList(String status, String menuName);

    ResponseResult putMenu(Menu menu);

    ResponseResult deleteMenu(Long id);

    ResponseResult treeSelect();

    ResponseResult roleMenuTreeSelect(Long id);
}
