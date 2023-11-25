package com.stone.auth.service;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stone.model.system.SysUser;
import com.stone.vo.system.LoginVo;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author stone
 * @since 2023-09-30
 */
public interface SysUserService extends IService<SysUser> {

    void modifyUserStatus(Long userId, Integer status);

    Map<String, String> login(LoginVo loginVo);//引入springSecurity之后,这个方法不会被调用了。

    Map<String, Object> getMenuInfo(HttpServletRequest req);

    Map<String, Object> getCurrentUser(Long userId);
}
