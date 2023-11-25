package com.stone.process.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.stone.model.process.ProcessRecord;

/**
 * <p>
 * 审批记录 服务类
 * </p>
 *
 * @author stone
 * @since 2023-10-07
 */
public interface OaProcessRecordService extends IService<ProcessRecord> {
    void record(Long processId, Integer status, String description);
}
