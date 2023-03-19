package com.czm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czm.constants.SystemConstants;
import com.czm.domain.ResponseResult;
import com.czm.domain.entity.Menu;
import com.czm.domain.entity.RoleMenu;
import com.czm.domain.vo.MenuTreeVo;
import com.czm.domain.vo.RoleMenuTreeVo;
import com.czm.enums.AppHttpCodeEnum;
import com.czm.exception.SystemException;
import com.czm.mapper.MenuMapper;
import com.czm.mapper.RoleMenuMapper;
import com.czm.service.MenuService;
import com.czm.utils.BeanCopyUtils;
import com.czm.utils.SecurityUtils;
import com.czm.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 菜单权限表(Menu)表服务实现类
 *
 * @author makejava
 * @since 2022-12-04 22:52:42
 */
@Service("menuService")
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private RoleMenuMapper roleMenuMapper;


    @Override
    public List<String> selectPermsByUserId(Long userId) {
        //判断如果id为1其为管理员查询所有权限
        if (userId == 1L){
            LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(Menu::getMenuType, SystemConstants.MENU,SystemConstants.BUTTON);
            wrapper.eq(Menu::getStatus,SystemConstants.STATUS_NORMAL);
            List<Menu> menus = list(wrapper);
            List<String> collect = menus.stream()
                    .map(menu -> menu.getPerms())
                    .collect(Collectors.toList());
            return collect;
        }
        //查询对应权限
        return getBaseMapper().selectPermsByUserId(userId);
    }

    @Override
    public List<Menu> selectRouterMenuTreeByUserId(Long userId) {
        List<Menu> menus = null;
        //如果id为1则返回所有菜单
        if (SecurityUtils.isAdmin()){
            LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(Menu::getMenuType,"C","M");
            wrapper.eq(Menu::getStatus,SystemConstants.STATUS_NORMAL);
            wrapper.orderByAsc(Menu::getId,Menu::getOrderNum);
            menus=list();
        }else{
            //返回对应菜单
            menus = getBaseMapper().selectRouterMenuTreeByUserId(userId);
        }
        //对menus进行tree的排序
        List<Menu> menuTree=builderMenuTree(menus,0L);
        return menuTree;
    }

    @Override
    public ResponseResult MenuList(String status, String menuName) {
        /*
        需要展示菜单列表，不需要分页。

    ​	可以针对菜单名进行模糊查询

    ​	也可以针对菜单的状态进行查询。

    ​	菜单要按照父菜单id和orderNum进行排序
         */
        LambdaUpdateWrapper<Menu> wrapper = new LambdaUpdateWrapper<>();
        //查询status
        wrapper.eq(StringUtils.hasText(status),Menu::getStatus,status);
        //模糊查询菜单名
        wrapper.like(StringUtils.hasText(menuName),Menu::getMenuName,menuName);
        //要按照父菜单id和orderNum排序
        wrapper.orderByAsc(Menu::getOrderNum,Menu::getId);
        List<Menu> menus = list(wrapper);
        return ResponseResult.okResult(menus);
    }

    @Override
    public ResponseResult putMenu(Menu menu) {
        //menu的id不能等于ParentId，如果等于，那么抛出异常
        if (menu.getId().equals(menu.getParentId())){
            throw new SystemException(AppHttpCodeEnum.MENU_ID_ERROR);
        }
        LambdaUpdateWrapper<Menu> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Menu::getId,menu.getId());
        update(menu,wrapper);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult deleteMenu(Long id) {
        //根据id删除菜单，但是有子菜单不能删除
        //先根据id，以他作为父类id查询所有菜单，如果查询非空，那么该菜单有子菜单
        LambdaQueryWrapper<Menu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Menu::getParentId,id);
        List<Menu> list = list(queryWrapper);
        if (list.size()>0){
            return ResponseResult.errorResult(500,"删除失败，存在子菜单不允许删除");
        }
        removeById(id);
        return ResponseResult.okResult();
    }

    private List<Menu> builderMenuTree(List<Menu> menus, Long ParentId) {
        List<Menu> menuTree = menus.stream()
                .filter(menu -> menu.getParentId().equals(ParentId))
                .map(menu -> menu.setChildren(getChildren(menus, menu)))
                .collect(Collectors.toList());
        return menuTree;
    }

    private List<Menu> getChildren(List<Menu> menus, Menu menu) {
        List<Menu> menuTree = menus.stream()
                .filter(m -> m.getParentId().equals(menu.getId()))
                .map(m -> m.setChildren(getChildren(menus, m)))
                .collect(Collectors.toList());
        return menuTree;
    }

    @Override
    public ResponseResult treeSelect() {
//      获取新增角色的功能，菜单要以树状返回
        List<Menu> menus = list();
        List<MenuTreeVo> menuTreeVos = BeanCopyUtils.copyBeanList(menus, MenuTreeVo.class);
        List<MenuTreeVo> menuTreeVoList = menuTreeVos.stream()
                .map(menuTreeVo -> menuTreeVo.setLabel(menuTreeVo.getMenuName()))
                .collect(Collectors.toList());
        //把menu转换为树状
        List<MenuTreeVo> menuTreeVo =  changeTree(0L,menuTreeVoList);
        return ResponseResult.okResult(menuTreeVo);
    }

    @Override
    public ResponseResult roleMenuTreeSelect(Long id) {
        //根据角色id查询菜单树
        //根据用户id拿到所以的菜单id
        LambdaQueryWrapper<RoleMenu> RMWrapper =  new LambdaQueryWrapper<>();
        RMWrapper.eq(RoleMenu::getRoleId,id);
        List<Long> menuIds = roleMenuMapper.selectList(RMWrapper).stream()
                .map(roleMenu -> roleMenu.getMenuId())
                .collect(Collectors.toList());
        //根据menuIds查询所有的菜单信息
        List<Menu> menus = list();
        List<MenuTreeVo> menuTreeVos = BeanCopyUtils.copyBeanList(menus, MenuTreeVo.class).stream()
                .map(menuTreeVo -> menuTreeVo.setLabel(menuTreeVo.getMenuName()))
                .collect(Collectors.toList());
        //把menu转换为树状

        List<MenuTreeVo> menuTreeVoList = changeTree(0L,menuTreeVos);
        //角色关联的菜单权限列表

        return ResponseResult.okResult(new RoleMenuTreeVo(menuTreeVoList,menuIds));
    }

    private List<MenuTreeVo> changeTree(Long i, List<MenuTreeVo> menuTreeVoList) {
        return menuTreeVoList.stream()
                .filter(menuTreeVo -> menuTreeVo.getParentId().equals(i))
                .map(menuTreeVo -> menuTreeVo.setChildren(getChildren(menuTreeVo, menuTreeVoList)))
                .collect(Collectors.toList());
    }

    private List<MenuTreeVo> getChildren(MenuTreeVo menuTreeVo, List<MenuTreeVo> menuTreeVoList) {
        return menuTreeVoList.stream()
                .filter(menuTreeVo1 -> menuTreeVo.getId().equals(menuTreeVo1.getParentId()))
                .map(menuTreeVo12 -> menuTreeVo12.setChildren(getChildren(menuTreeVo12, menuTreeVoList)))
                .collect(Collectors.toList());
    }
}
