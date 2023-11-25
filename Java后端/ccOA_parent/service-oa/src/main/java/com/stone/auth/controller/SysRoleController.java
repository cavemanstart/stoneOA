package com.stone.auth.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stone.auth.service.SysRoleService;
import com.stone.auth.service.SysUserRoleService;
import com.stone.common.result.Result;
import com.stone.model.system.SysRole;
import com.stone.model.system.SysUserRole;
import com.stone.vo.system.AssignRoleVo;
import com.stone.vo.system.SysRoleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Api(tags = "角色管理接口")
@RestController //返回json格式的数据
@RequestMapping("/admin/system/sysRole")
public class SysRoleController {
    @Autowired
    private SysRoleService service;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    //查询所有的角色
    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @GetMapping("allRoles")
    @ApiOperation("查询所有角色")
    public Result getAll(){
        List list = service.list();
        return Result.ok(list);
    }
    @ApiOperation("角色条件分页查询")
    @GetMapping("{current}/{size}")
    public Result getPagesByRoleName(@PathVariable Long current, @PathVariable Long size, SysRoleQueryVo sysRoleQueryVo){
        Page<SysRole> pageParam = new Page<>(current,size);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if(!StringUtils.isEmpty(sysRoleQueryVo.getRoleName())){
            wrapper.like(SysRole::getRoleName,sysRoleQueryVo.getRoleName());
        }
        IPage<SysRole> pageModel = service.page(pageParam, wrapper);
        return Result.ok(pageModel);
    }
    @PreAuthorize("hasAuthority('bnt.sysRole.add')")
    @ApiOperation("添加角色")
    @PostMapping("addRole")
    public Result addRole(@RequestBody SysRole role){
        boolean res = service.save(role);
        return res?Result.ok():Result.fail();
    }
    @PreAuthorize("hasAuthority('bnt.sysRole.list')")
    @ApiOperation("根据id获取角色")
    @GetMapping("getRoleById/{id}")
    public Result addRole(@PathVariable  Long id){
        SysRole role = service.getById(id);
        return Result.ok(role);
    }
    @PreAuthorize("hasAuthority('bnt.sysRole.update')")
    @ApiOperation("根据id修改角色")
    @PutMapping("updateRole")
    public Result updateRole(@RequestBody SysRole role){
        boolean res = service.updateById(role);
        return res?Result.ok():Result.fail();
    }
    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @ApiOperation("根据id删除角色")
    @DeleteMapping("removeRole/{id}")
    public Result removeRole(@PathVariable Long id){
        boolean removed = service.removeById(id);
        return removed?Result.ok():Result.fail();
    }
    @PreAuthorize("hasAuthority('bnt.sysRole.remove')")
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation("根据id批量删除")
    @DeleteMapping("removeBatchRole")
    public Result removeBatchRole(@RequestBody List<Long> list){
        boolean res = service.removeByIds(list);
        return res?Result.ok():Result.fail();
    }
    //根据用户id获取角色列表
    @ApiOperation("根据用户id获取角色列表")
    @GetMapping("getRoleListByUserId/{userId}")
    public Result getRoleListById(@PathVariable Long userId){
        List<Long> roleIds =  service.getRoleListByUserId(userId);
        return Result.ok(roleIds);
    }
    //为用户重新分配角色
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasAuthority('bnt.sysUser.assignRole')")
    @ApiOperation("为用户分配角色")
    @PostMapping("assign")
    public Result assign(@RequestBody AssignRoleVo assignRoleVo){
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId,assignRoleVo.getUserId());
        sysUserRoleService.remove(wrapper);
        List<Long> roleIds = assignRoleVo.getRoleIdList();
        for(Long roleId : roleIds){
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(assignRoleVo.getUserId());
            sysUserRole.setRoleId(roleId);
            boolean saved = sysUserRoleService.save(sysUserRole);
            if(!saved) return Result.fail();
        }
        return Result.ok();
    }
}
