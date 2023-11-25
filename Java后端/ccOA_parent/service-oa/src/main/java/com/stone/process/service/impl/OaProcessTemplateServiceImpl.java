package com.stone.process.service.impl;

import com.stone.model.process.ProcessTemplate;
import com.stone.process.mapper.OaProcessTemplateMapper;
import com.stone.process.service.OaProcessService;
import com.stone.process.service.OaProcessTemplateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * <p>
 * 审批模板 服务实现类
 * </p>
 *
 * @author stone
 * @since 2023-10-05
 */
@Service
public class OaProcessTemplateServiceImpl extends ServiceImpl<OaProcessTemplateMapper, ProcessTemplate> implements OaProcessTemplateService {
    @Autowired
    private OaProcessService processService;
    @Override
    public boolean publish(Long id) {
        ProcessTemplate processTemplate = baseMapper.selectById(id);
        if(!StringUtils.isEmpty(processTemplate.getProcessDefinitionPath())){
            processService.deployedProcess(processTemplate.getProcessDefinitionPath());
            processTemplate.setStatus(1);
            baseMapper.updateById(processTemplate);
            return true;
        }
        return false;
    }
}
