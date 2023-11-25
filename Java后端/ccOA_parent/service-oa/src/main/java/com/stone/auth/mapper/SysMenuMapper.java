package com.stone.auth.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stone.model.system.SysMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author stone
 * @since 2023-10-01
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> getMenuListByUserId(@Param("userId") Long userId);
}
