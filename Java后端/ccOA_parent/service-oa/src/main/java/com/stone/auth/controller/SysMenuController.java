package com.stone.auth.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.stone.auth.service.SysMenuService;
import com.stone.common.result.Result;
import com.stone.model.system.SysMenu;
import com.stone.vo.system.AssginMenuVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 菜单表 前端控制器
 * </p>
 *
 * @author stone
 * @since 2023-10-01
 */
@Api(tags = "菜单管理")
@RestController
@RequestMapping("/admin/system/sysMenu")
public class SysMenuController {
    @Autowired
    private SysMenuService service;
    @ApiOperation("新增菜单")
    @PreAuthorize("hasAuthority('bnt.sysMenu.add')")
    @PostMapping("addMenu")
    public Result addMenu(@RequestBody SysMenu sysMenu){
        service.save(sysMenu);
        return Result.ok();
    }
    @ApiOperation("修改菜单")
    @PreAuthorize("hasAuthority('bnt.sysMenu.update')")
    @PutMapping("updateMenu")
    public Result updateMenu(@RequestBody SysMenu sysMenu){
        service.updateById(sysMenu);
        return Result.ok();
    }
    @ApiOperation("删除菜单")
    @PreAuthorize("hasAuthority('bnt.sysMenu.remove')")
    @DeleteMapping("removeMenu/{id}")
    public Result removeMenu(@PathVariable Long id){
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getParentId, id);
        int count = service.count(wrapper);
        if (count>0) {//有子菜单
            return Result.fail().message("含有子菜单，不能删除");
        }else{
            service.removeById(id);
            return Result.ok();
        }
    }
    @ApiOperation("菜单列表")
    @GetMapping("findNodes")
    public Result findNodes(){
        List<SysMenu> list  = service.findNodes();
        return Result.ok(list);
    }
    @ApiOperation("查询当前角色拥有菜单")
    @GetMapping("getMenuByRoleId/{roleId}")
    public Result getMenuByRoleId(@PathVariable Long roleId){
        List<SysMenu> menuList = service.getMenuByRoleId(roleId);
        return Result.ok(menuList);
    }
    @ApiOperation("为角色分配菜单")
    @PreAuthorize("hasAuthority('btn.sysRole.assignAuth')")
    @PostMapping("assignMenu")
    public Result assignMenu(@RequestBody AssginMenuVo assginMenuVo){
        service.assignMenu(assginMenuVo);
        return Result.ok();
    }
}

