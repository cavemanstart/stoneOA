package com.stone.process.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stone.auth.service.SysUserService;
import com.stone.common.result.Result;
import com.stone.model.process.Process;
import com.stone.model.process.ProcessTemplate;
import com.stone.model.process.ProcessType;
import com.stone.model.system.SysUser;
import com.stone.process.service.OaProcessService;
import com.stone.process.service.OaProcessTemplateService;
import com.stone.process.service.OaProcessTypeService;
import com.stone.security.custom.LoginUserInfoHelper;
import com.stone.vo.process.ApprovalVo;
import com.stone.vo.process.ProcessFormVo;
import com.stone.vo.process.ProcessVo;
import com.sun.mail.imap.ResyncData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.PageRanges;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

@Api("用户流程审批管理")
@RestController
@RequestMapping(value="/admin/process")
@CrossOrigin
public class UserProcessController {
    @Autowired
    private OaProcessTypeService processTypeService;
    @Autowired
    private OaProcessTemplateService processTemplateService;
    @Autowired
    private OaProcessService processService;
    @Autowired
    private SysUserService userService;
    @ApiOperation("按格式获取审批类型及模板")
    @GetMapping("findAllProcesses")
    public Result findAllProcesses(){
        List<ProcessType> typeList = processTypeService.list();
        for(ProcessType type : typeList){
            LambdaQueryWrapper<ProcessTemplate> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ProcessTemplate::getProcessTypeId,type.getId());
            List<ProcessTemplate> templateList = processTemplateService.list(wrapper);
            type.setProcessTemplateList(templateList);
        }
        return Result.ok(typeList);
    }
    @ApiOperation("根据模板id获取模板信息")
    @GetMapping("getTemplateById/{id}")
    public Result getTemplateById(@PathVariable Long id){
        ProcessTemplate template = processTemplateService.getById(id);
        return Result.ok(template);
    }
    @ApiOperation("启动流程实例")
    @PostMapping("startUp")
    public Result startUp(@RequestBody ProcessFormVo processFormVo){
        processService.startUpProceses(processFormVo);
        return Result.ok();
    }
    @ApiOperation("查询当前用户待处理任务")
    @GetMapping("findBusiness/{current}/{size}")
    public Result findBusiness(@PathVariable Long current, @PathVariable Long size){
        Page<Process> page = new Page<>(current,size);
        IPage<ProcessVo> pageModel = processService.findBusiness(page);
        return Result.ok(pageModel);
    }
    @ApiOperation("查看审批详情")
    @GetMapping("showTaskInfo/{processId}")
    public Result showTaskInfo(@PathVariable Long processId){
        Map<String, Object> map = processService.showTaskInfo(processId);
        return Result.ok(map);
    }
    @ApiOperation("审批")
    @PostMapping("approve")
    public Result approve(@RequestBody ApprovalVo approvalVo){
        processService.approve(approvalVo);
        return Result.ok();
    }
    @ApiOperation("已处理")
    @GetMapping("findProcessed/{current}/{size}")
    public Result findProcessed(@PathVariable Long current, @PathVariable Long size){
        Page<Process> pageParam = new Page<>(current, size);
        IPage<ProcessVo> pageModel = processService.findProcessed(pageParam);
        return Result.ok(pageModel);
    }
    @ApiOperation("已发起")
    @GetMapping("findStarted/{current}/{size}")
    public Result findStarted(@PathVariable Long current, @PathVariable Long size){
        Page<ProcessVo> pageParam = new Page<>(current,size);
        IPage<ProcessVo> pageModel = processService.findStarted(pageParam);
        return Result.ok(pageModel);
    }
    @ApiOperation("获取当前用户信息")
    @GetMapping("getCurrentUser")
    public Result getCurrentUser(){
        Map<String, Object> map = userService.getCurrentUser(LoginUserInfoHelper.getUserId());
        return Result.ok(map);
    }
}
