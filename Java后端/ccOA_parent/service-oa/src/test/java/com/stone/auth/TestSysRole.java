package com.stone.auth;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stone.auth.mapper.SysRoleMapper;
import com.stone.auth.service.SysRoleService;
import com.stone.model.system.SysRole;
import com.stone.model.system.SysUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class TestSysRole {
    @Autowired
    private SysRoleMapper mapper;
    @Autowired
    private SysRoleService service;
    @Test
    public void test1(){
        SysRole sysRole = mapper.selectById(1);
        System.out.println(sysRole);
    }
    @Test
    public void testInsert(){
        SysRole sysRole = new SysRole();
        sysRole.setRoleName("普通用户");
        sysRole.setRoleCode("NORMAL");
        sysRole.setDescription("普通用户");
        System.out.println(mapper.insert(sysRole));
        System.out.println(sysRole.getId());
    }
    @Test
    public void wrapperTest(){
        QueryWrapper<SysRole> wrapper = new QueryWrapper<>();
        wrapper.eq("role_name","普通用户").or().orderByDesc();
        List<SysRole> sysRoles = mapper.selectList(wrapper);
        sysRoles.forEach(sysRole -> System.out.println(sysRole));
    }
    @Test
    public void testService(){
        List<SysRole> list = service.list();
        list.forEach(sysRole -> System.out.println(sysRole));
    }
}
