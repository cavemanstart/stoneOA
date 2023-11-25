package com.stone.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stone.auth.mapper.SysUserMapper;
import com.stone.auth.service.SysMenuService;
import com.stone.auth.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stone.common.config.exception.StoneException;
import com.stone.model.system.SysUser;
import com.stone.utils.JwtHelper;
import com.stone.utils.MD5;
import com.stone.vo.system.LoginVo;
import com.stone.vo.system.RouterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author stone
 * @since 2023-09-30
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Autowired
    private SysMenuService sysMenuService;
    @Override
    public void modifyUserStatus(Long userId, Integer status) {
        SysUser sysUser = baseMapper.selectById(userId);
        sysUser.setStatus(status);
        baseMapper.updateById(sysUser);
    }

    @Override
    public Map<String, String> login(LoginVo loginVo) {
        String username = loginVo.getUsername();
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername,username);
        SysUser sysUser = baseMapper.selectOne(wrapper);//获取用户对象
        if(sysUser==null){
            throw new StoneException(250,"用户不存在");
        }
        if(sysUser.getStatus()==0){
            throw  new StoneException(252,"用户禁用");
        }
        String password = sysUser.getPassword();
        if(StringUtils.isEmpty(password)) throw new StoneException(245,"密码异常");
        String inputPw = MD5.encrypt(loginVo.getPassword());
        if(!password.equals(inputPw)){
            throw new StoneException(251,"密码不正确");
        }
        String token = JwtHelper.createToken(sysUser.getId(), sysUser.getUsername());//获取token
        Map<String, String> map = new HashMap<>();
        map.put("token",token);
        return map;
    }

    @Override
    public Map<String, Object> getMenuInfo(HttpServletRequest req) {
        String token = req.getHeader("token");
        Long userId = JwtHelper.getUserId(token);
        SysUser sysUser = baseMapper.selectById(userId);//查询到用户的信息
        //根据用户id获取可以操作的菜单列表
        List<RouterVo> meauList = sysMenuService.getMenuListByUserId(userId);
        //根据用户id获取可以操作的button列表
        List<String> buttonList = sysMenuService.getButtonListByUserId(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("roles","[admin]");
        map.put("name",sysUser.getName());
        map.put("avatar","https://oss.aliyuncs.com/aliyun_id_photo_bucket/default_handsome.jpg");
        map.put("buttons",buttonList);
        map.put("routers",meauList);
        return map;
    }

    @Override
    public Map<String, Object> getCurrentUser(Long userId) {
        SysUser sysUser = baseMapper.selectById(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("name",sysUser.getName());
        map.put("phone",sysUser.getPhone());
        return map;
    }
}

