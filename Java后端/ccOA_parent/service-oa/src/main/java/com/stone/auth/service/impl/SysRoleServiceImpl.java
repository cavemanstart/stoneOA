package com.stone.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stone.auth.mapper.SysRoleMapper;
import com.stone.auth.service.SysRoleService;
import com.stone.auth.service.SysUserRoleService;
import com.stone.model.system.SysRole;
import com.stone.model.system.SysUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {
    @Autowired
    private SysUserRoleService userRoleService;
    @Override
    public List<Long> getRoleListByUserId(Long userId) {
        LambdaQueryWrapper<SysUserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserRole::getUserId,userId);
        List<SysUserRole> userRoleList = userRoleService.list(wrapper);
        List<Long> roleIds = userRoleList.stream().map(c -> c.getRoleId()).collect(Collectors.toList());
        return roleIds;
    }
}
