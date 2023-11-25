package com.stone.wechat.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stone.model.wechat.Menu;
import com.stone.vo.wechat.MenuVo;
import com.stone.wechat.config.WeChatMpConfig;
import com.stone.wechat.mapper.MenuMapper;
import com.stone.wechat.service.MenuService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * 菜单 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-02-16
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    @Autowired
    private WxMpService wxMpService;
    //获取全部菜单
    @Override
    public List<MenuVo> findMenuInfo() {
        List<MenuVo> list = new ArrayList<>();
        //1 查询所有菜单list集合
        List<Menu> menuList = baseMapper.selectList(null);
        for(Menu menu: menuList){
            if(menu.getParentId().longValue()==0){
                list.add(helper(menuList,menu));
            }
        }
        return list;
    }

    @Override
    public void syncMenu() {
        //1 菜单数据查询出来，封装微信要求菜单格式
        List<MenuVo> menuVoList = this.findMenuInfo();
        //菜单
        JSONArray buttonList = new JSONArray();
        for(MenuVo oneMenuVo : menuVoList) {
            JSONObject one = new JSONObject();
            one.put("name", oneMenuVo.getName());
            if(CollectionUtils.isEmpty(oneMenuVo.getChildren())) {
                one.put("type", oneMenuVo.getType());
                one.put("url", "http://4ar0h123.beesnat.com/#"+oneMenuVo.getUrl());
            } else {
                JSONArray subButton = new JSONArray();
                for(MenuVo twoMenuVo : oneMenuVo.getChildren()) {
                    JSONObject view = new JSONObject();
                    view.put("type", twoMenuVo.getType());
                    if(twoMenuVo.getType().equals("view")) {
                        view.put("name", twoMenuVo.getName());
                        //H5页面地址
                        view.put("url", "http://4ar0h123.beesnat.com/#"+twoMenuVo.getUrl());
                    } else {
                        view.put("name", twoMenuVo.getName());
                        view.put("key", twoMenuVo.getMeunKey());
                    }
                    subButton.add(view);
                }
                one.put("sub_button", subButton);
            }
            buttonList.add(one);
        }
        //菜单
        JSONObject button = new JSONObject();
        button.put("button", buttonList);

        //2 调用工具里面的方法实现菜单推送
        try {
            wxMpService.getMenuService().menuCreate(button.toString());
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void removeMenu() {
        try {
            wxMpService.getMenuService().menuDelete();
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }

    private MenuVo helper(List<Menu> menuList, Menu menu){
        MenuVo menuVo = new MenuVo();
        menuVo.setChildren(new ArrayList<>());
        BeanUtils.copyProperties(menu,menuVo);
        for(Menu it: menuList){
            if(it.getParentId().longValue()==menuVo.getId().longValue()){
                menuVo.getChildren().add(helper(menuList,it));
            }
        }
        return menuVo;
    }
}
