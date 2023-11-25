package com.stone.auth.utils;

import com.stone.model.system.SysMenu;
import lombok.experimental.Helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuHelper {

    public static List<SysMenu> build(List<SysMenu> sysMenus) {
        Map<Long,List<SysMenu>> map = new HashMap<>();
        for(SysMenu it: sysMenus){
            if(!map.containsKey(it.getParentId().longValue())){
               map.put(it.getParentId().longValue(),new ArrayList<>());
            }
            map.get(it.getParentId().longValue()).add(it);
        }
        List<SysMenu> res = new ArrayList<>();
        for(SysMenu cur: map.get(0L)){
            res.add(helper(map,cur));
        }
        return res;
    }
    public static SysMenu helper(Map<Long, List<SysMenu>> map, SysMenu cur){
        cur.setChildren(new ArrayList<>());
        if(map.containsKey(cur.getId())){
            for(SysMenu it:map.get(cur.getId())){
                cur.getChildren().add(helper(map,it));
            }
        }
        return cur;
    }
}
