package com.stone.wechat.controller;


import com.stone.common.result.Result;
import com.stone.vo.wechat.MenuVo;
import com.stone.wechat.service.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 菜单 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-02-16
 */
@Api(tags = "微信公众号管理")
@RestController
@RequestMapping("/admin/wechat/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @ApiOperation(value = "获取全部菜单")
    @GetMapping("findMenuInfo")

    public Result findMenuInfo() {
        List<MenuVo> menuList = menuService.findMenuInfo();
        return Result.ok(menuList);
    }
    @ApiOperation("同步菜单")
    @GetMapping("syncMenu")
    public Result syncMenu(){
        menuService.syncMenu();
        return Result.ok();
    }
    @ApiOperation(value = "删除菜单")
    @DeleteMapping("removeMenu")
    public Result removeMenu() {
        menuService.removeMenu();
        return Result.ok();
    }
}

