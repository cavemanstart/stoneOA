package com.stone.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stone.model.system.SysMenu;
import com.stone.vo.system.AssginMenuVo;
import com.stone.vo.system.RouterVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author stone
 * @since 2023-10-01
 */
public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> findNodes();

    List<SysMenu> getMenuByRoleId(Long roleId);

    void assignMenu(AssginMenuVo assginMenuVo);

    List<RouterVo> getMenuListByUserId(Long userId);

    List<String> getButtonListByUserId(Long userId);
}
