package com.stone.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stone.auth.service.SysMenuService;
import com.stone.auth.service.SysUserService;
import com.stone.model.system.SysUser;
import com.stone.security.custom.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserService sysUserService;
    
    @Autowired
    private SysMenuService sysMenuService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据用户名进行查询
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        SysUser sysUser = sysUserService.getOne(wrapper);
        if(null == sysUser) {
            throw new UsernameNotFoundException("用户名不存在！");
        }

        if(sysUser.getStatus().intValue() == 0) {
            throw new RuntimeException("账号已停用");
        }
//        //根据userid查询用户操作权限数据
        List<String> userButtonList = sysMenuService.getButtonListByUserId(sysUser.getId());
//        //创建list集合，封装最终权限数据
        List<SimpleGrantedAuthority> authList = new ArrayList<>();
//        //查询list集合遍历
        for (String perm : userButtonList) {
            authList.add(new SimpleGrantedAuthority(perm.trim()));
        }
        return new CustomUser(sysUser, authList);
    }
}
