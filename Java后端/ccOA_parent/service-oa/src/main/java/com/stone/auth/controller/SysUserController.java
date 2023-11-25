package com.stone.auth.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stone.auth.service.SysRoleService;
import com.stone.auth.service.SysUserRoleService;
import com.stone.auth.service.SysUserService;
import com.stone.common.result.Result;
import com.stone.model.system.SysRole;
import com.stone.model.system.SysUser;
import com.stone.model.system.SysUserRole;
import com.stone.utils.MD5;
import com.stone.vo.system.SysUserQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javassist.expr.NewArray;
import lombok.val;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author stone
 * @since 2023-09-30
 */
@RestController
@RequestMapping("/admin/system/sysUser")
@Api(tags = "用户管理接口")
public class SysUserController {
    @Autowired
    private SysUserService service;
    @Autowired
    private SysRoleService roleService;
    @Autowired
    private SysUserRoleService userRoleService;
    //修改用户状态，1:可用，0:禁用
    @ApiOperation("修改用户状态")
    @PreAuthorize("hasAuthority('bnt.sysRole.update')")
    @GetMapping("modifyUserStatus/{userId}/{status}")
    public Result modifyUserStatus(@PathVariable Long userId, @PathVariable Integer status){
        service.modifyUserStatus(userId, status);
        return Result.ok();
    }
    @ApiOperation("用户分页查询")
    @GetMapping("{current}/{size}")
    public Result getUserPages(@PathVariable Long current, @PathVariable Long size, SysUserQueryVo userQueryVo){
        Page<SysUser> page = new Page<>(current,size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(userQueryVo.getKeyword())){
            wrapper.like(SysUser::getUsername,userQueryVo.getKeyword());
        }
        if(!StringUtils.isEmpty(userQueryVo.getCreateTimeBegin())){
            wrapper.ge(SysUser::getCreateTime,userQueryVo.getCreateTimeBegin());
        }
        if(!StringUtils.isEmpty(userQueryVo.getCreateTimeEnd())){
            wrapper.ge(SysUser::getCreateTime,userQueryVo.getCreateTimeEnd());
        }
        IPage<SysUser>  rb = service.page(page,wrapper);
        List<SysUser> users = rb.getRecords();
        List<SysRole> allRoles = roleService.list();
        for(SysUser user: users){
            LambdaQueryWrapper<SysUserRole> wrapper1 = new LambdaQueryWrapper<>();
            wrapper1.eq(SysUserRole::getUserId,user.getId());
            List<SysUserRole> userRoleList = userRoleService.list(wrapper1);
            List<Long> roleIds = userRoleList.stream().map(it -> it.getRoleId()).collect(Collectors.toList());
            List<SysRole> roleList = new ArrayList<>();
            for(SysRole role:allRoles){
                if(roleIds.contains(role.getId())){
                    roleList.add(role);
                }
            }
            user.setRoleList(roleList);
        }
        rb.setRecords(users);
        return Result.ok(rb);
    }
    @ApiOperation("根据ID查询用户")
    @GetMapping("getUserById/{id}")
    public Result getUserById(@PathVariable Long id){
        SysUser sysUser = service.getById(id);
        return Result.ok(sysUser);
    }
    @ApiOperation("根据ID删除用户")
    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @DeleteMapping("removeUserById/{id}")
    public Result removeUserById(@PathVariable Long id){
        boolean removed = service.removeById(id);
        return removed?Result.ok():Result.fail();
    }
    @ApiOperation("根据ID批量删除用户")
    @PreAuthorize("hasAuthority('btn.sysUser.remove')")
    @Transactional(rollbackFor = Exception.class)
    @DeleteMapping("batchRmUserById")
    public Result removeUserById(@RequestBody List<Long> idList){
        boolean removed = service.removeByIds(idList);
        return removed?Result.ok():Result.fail();
    }
    @ApiOperation("根据Id修改用户")
    @PreAuthorize("hasAuthority('btn.sysUser.update')")
    @PutMapping("updateUser")
    public Result updateUser(@RequestBody SysUser user){
        boolean updated = service.updateById(user);
        return updated?Result.ok():Result.fail();
    }
    @ApiOperation("添加用户")
    @PreAuthorize("hasAuthority('btn.sysUser.add')")
    @PostMapping("addUser")
    public Result addUser(@RequestBody SysUser user){
        String encryptedPw = MD5.encrypt(user.getPassword());
        user.setPassword(encryptedPw);
        service.save(user);
        return Result.ok();
    }
}

