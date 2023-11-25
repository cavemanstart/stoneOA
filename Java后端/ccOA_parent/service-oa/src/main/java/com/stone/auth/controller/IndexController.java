package com.stone.auth.controller;

import com.stone.auth.service.SysUserService;
import com.stone.common.result.Result;

import com.stone.vo.system.LoginVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Api(tags = "后台登录管理")
@RestController
@RequestMapping("admin/system/index")
public class IndexController {
    @Autowired
    private SysUserService sysUserService;
    @ApiOperation("用户登录")
    @PostMapping("login")//这个接口被springSecurity接管了
    public Result login(@RequestBody LoginVo loginVo){
        Map<String, String> map = sysUserService.login(loginVo);
        return Result.ok(map);
    }
    @ApiOperation("获取用户菜单列表")
    @GetMapping("menuInfo")
    public Result info(HttpServletRequest req){
        Map<String, Object> map = sysUserService.getMenuInfo(req) ;
        return Result.ok(map);
    }
    @PostMapping("logout")
    public Result logout(){
        return Result.ok();
    }
}
