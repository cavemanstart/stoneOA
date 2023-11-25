package com.stone.process.service;

import com.stone.model.process.ProcessTemplate;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 审批模板 服务类
 * </p>
 *
 * @author stone
 * @since 2023-10-05
 */
public interface OaProcessTemplateService extends IService<ProcessTemplate> {

    boolean publish(Long id);
}
