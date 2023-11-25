package com.stone.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stone.auth.mapper.SysMenuMapper;
import com.stone.auth.service.SysMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stone.auth.service.SysRoleMenuService;
import com.stone.auth.service.SysUserService;
import com.stone.auth.utils.MenuHelper;
import com.stone.common.result.Result;
import com.stone.model.system.SysMenu;
import com.stone.model.system.SysRoleMenu;
import com.stone.model.system.SysUser;
import com.stone.utils.JwtHelper;
import com.stone.vo.system.AssginMenuVo;
import com.stone.vo.system.MetaVo;
import com.stone.vo.system.RouterVo;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.handler.UserRoleAuthorizationInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author stone
 * @since 2023-10-01
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
    @Autowired
    private SysRoleMenuService sysRoleMenuService;
    @Autowired
    private SysMenuService sysMenuService;
    @Autowired
    private SysUserService sysUserService;
    @Override
    public List<SysMenu> findNodes() {
        List<SysMenu> sysMenus = baseMapper.selectList(null);
        List<SysMenu> res = MenuHelper.build(sysMenus);
        return res;
    }
    @Override
    public List<SysMenu> getMenuByRoleId(Long roleId) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getStatus,1);
        List<SysMenu> allMenus = baseMapper.selectList(wrapper);//获取所有菜单
        LambdaQueryWrapper<SysRoleMenu> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(SysRoleMenu::getRoleId,roleId);
        List<SysRoleMenu> roleMenuList = sysRoleMenuService.list(wrapper1);//获取roleId对应的SysRoleMenu
        //获取roleMenuList里面的MenuId
        List<Long> menuListByRoleId = roleMenuList.stream().map(c -> c.getMenuId()).collect(Collectors.toList());
        for(SysMenu it : allMenus){
            if(menuListByRoleId.contains(it.getId())){
                it.setSelect(true);
            }else it.setSelect(false);
        }
        List<SysMenu> res = MenuHelper.build(allMenus);
        return res;
    }

    @Override
    public void assignMenu(AssginMenuVo assginMenuVo) {
        Long roleId = assginMenuVo.getRoleId();
        List<Long> menuList = assginMenuVo.getMenuIdList();
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId,roleId);
        sysRoleMenuService.remove(wrapper);
        for(Long id:menuList){
            if(StringUtils.isEmpty(id)) continue;
            SysRoleMenu sysRoleMenu  = new SysRoleMenu();
            sysRoleMenu.setRoleId(roleId);
            sysRoleMenu.setMenuId(id);
            sysRoleMenuService.save(sysRoleMenu);
        }
    }

    @Override
    public List<RouterVo> getMenuListByUserId(Long userId) {
        List<SysMenu> menuList  = null;
        //判断用户是管理员还是普通用户
        if(userId.longValue()==1){
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus,1).orderByAsc(SysMenu::getSortValue);
            menuList = baseMapper.selectList(wrapper);
        }else{//不是管理员，根据userId查询可以操作的菜单列表
            menuList = baseMapper.getMenuListByUserId(userId);
        }
        List<SysMenu> menuTreeList = MenuHelper.build(menuList);
        List<RouterVo> menuRouterList = buildRouter(menuTreeList);
        return menuRouterList;
    }
    //将菜单构建成路由结构
    private List<RouterVo> buildRouter(List<SysMenu> menus) {
        List<RouterVo> routers = new ArrayList<>();
        for(SysMenu it:menus){
            RouterVo router = new RouterVo();
            router.setHidden(false);
            router.setAlwaysShow(false);
            router.setPath(getRouterPath(it));
            router.setComponent(it.getComponent());
            router.setMeta(new MetaVo(it.getName(), it.getIcon()));
            List<SysMenu> children = it.getChildren();
            if(it.getType().intValue()==1){//加载菜单下面的隐藏路由
                List<SysMenu> hiddenMenus = children.stream()
                        .filter(item -> !StringUtils.isEmpty(item.getComponent()))
                        .collect(Collectors.toList());
                for(SysMenu item : hiddenMenus){
                    RouterVo hiddenRouter = new RouterVo();
                    //true 隐藏路由
                    hiddenRouter.setHidden(true);
                    hiddenRouter.setAlwaysShow(false);
                    hiddenRouter.setPath(getRouterPath(item));
                    hiddenRouter.setComponent(item.getComponent());
                    hiddenRouter.setMeta(new MetaVo(item.getName(), item.getIcon()));
                    routers.add(hiddenRouter);
                }
            } else {
                if(!CollectionUtils.isEmpty(children)) {
                    if(children.size() > 0) {
                        router.setAlwaysShow(true);
                    }
                    //递归
                    router.setChildren(buildRouter(children));
                }
            }
            routers.add(router);
        }
        return routers;
    }
    private String getRouterPath(SysMenu menu) {
        String routerPath = "/" + menu.getPath();
        if(menu.getParentId().intValue() != 0) {
            routerPath = menu.getPath();
        }
        return routerPath;
    }

    @Override
    public List<String> getButtonListByUserId(Long userId) {
        List<SysMenu> menus = null;
        if(userId.longValue()==1){
            LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysMenu::getStatus,1);
            menus = baseMapper.selectList(wrapper);
        }else{
            menus = baseMapper.getMenuListByUserId(userId);
        }
        List<String> buttonList = menus.stream().filter(it -> it.getType() == 2)
                .map(it -> it.getPerms()).collect(Collectors.toList());
        return buttonList;
    }
}
