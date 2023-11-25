package com.stone.wechat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stone.model.wechat.Menu;
import com.stone.vo.wechat.MenuVo;

import java.util.List;

/**
 * <p>
 * 菜单 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-02-16
 */
public interface MenuService extends IService<Menu> {

    //获取全部菜单
    List<MenuVo> findMenuInfo();

    void syncMenu();

    void removeMenu();
}
