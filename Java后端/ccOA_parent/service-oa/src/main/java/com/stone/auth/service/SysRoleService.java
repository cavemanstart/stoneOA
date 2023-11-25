package com.stone.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stone.model.system.SysRole;
import org.springframework.stereotype.Service;

import java.util.List;


public interface SysRoleService extends IService<SysRole> {

    List<Long> getRoleListByUserId(Long userId);
}
