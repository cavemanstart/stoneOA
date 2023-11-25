package com.stone.process.service.impl;

import com.stone.auth.service.SysUserService;
import com.stone.model.process.ProcessRecord;
import com.stone.model.system.SysUser;
import com.stone.process.mapper.OaProcessRecordMapper;
import com.stone.process.service.OaProcessRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stone.security.custom.LoginUserInfoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.ProxySelector;

/**
 * <p>
 * 审批记录 服务实现类
 * </p>
 *
 * @author stone
 * @since 2023-10-07
 */
@Service
public class OaProcessRecordServiceImpl extends ServiceImpl<OaProcessRecordMapper, ProcessRecord> implements OaProcessRecordService {
    @Autowired
    private SysUserService userService;
    @Override
    public void record(Long processId, Integer status, String description) {
        ProcessRecord processRecord = new ProcessRecord();
        processRecord.setProcessId(processId);
        processRecord.setStatus(status);
        processRecord.setDescription(description);
        SysUser sysUser = userService.getById(LoginUserInfoHelper.getUserId());
        processRecord.setOperateUser(sysUser.getName());
        processRecord.setOperateUserId(sysUser.getId());
        baseMapper.insert(processRecord);
    }
}
