package com.stone.process.service;
import com.stone.model.process.Process;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stone.vo.process.ApprovalVo;
import com.stone.vo.process.ProcessFormVo;
import com.stone.vo.process.ProcessQueryVo;
import com.stone.vo.process.ProcessVo;

import java.util.Map;

/**
 * <p>
 * 审批类型 服务类
 * </p>
 *
 * @author stone
 * @since 2023-10-06
 */
public interface OaProcessService extends IService<Process> {

    IPage<ProcessVo> getPage(Page<ProcessVo> page, ProcessQueryVo processQueryVo);
    void deployedProcess(String path);

    void startUpProceses(ProcessFormVo processFormVo);

    IPage<ProcessVo> findBusiness(Page<Process> page);

    Map<String, Object> showTaskInfo(Long processId);

    void approve(ApprovalVo approvalVo);

    IPage<ProcessVo> findProcessed(Page<Process> pageParam);

    IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam);
}
