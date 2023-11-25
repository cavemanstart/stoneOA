package com.stone.process.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stone.auth.service.SysUserService;
import com.stone.model.process.Process;
import com.stone.model.process.ProcessRecord;
import com.stone.model.process.ProcessTemplate;
import com.stone.model.system.SysUser;
import com.stone.process.mapper.OaProcessMapper;
import com.stone.process.service.MessageService;
import com.stone.process.service.OaProcessRecordService;
import com.stone.process.service.OaProcessService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stone.process.service.OaProcessTemplateService;
import com.stone.security.custom.LoginUserInfoHelper;
import com.stone.vo.process.ApprovalVo;
import com.stone.vo.process.ProcessFormVo;
import com.stone.vo.process.ProcessQueryVo;
import com.stone.vo.process.ProcessVo;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.EndEvent;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.SequenceFlow;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricActivityInstanceQuery;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricTaskInstanceQuery;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReactiveListCommands;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.InputStream;
import java.util.*;
import java.util.zip.ZipInputStream;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author stone
 * @since 2023-10-06
 */
@Service
public class OaProcessServiceImpl extends ServiceImpl<OaProcessMapper, Process> implements OaProcessService {
    @Autowired
    RepositoryService repositoryService;
    @Autowired
    RuntimeService runtimeService;
    @Autowired
    private SysUserService userService;
    @Autowired
    private OaProcessTemplateService templateService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private OaProcessRecordService recordService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private MessageService messageService;
    @Override
    public IPage<ProcessVo> getPage(Page<ProcessVo> page, ProcessQueryVo processQueryVo){
        IPage<ProcessVo> pageModel = baseMapper.selectPage(page,processQueryVo);
        return pageModel;
    }

    @Override
    public void deployedProcess(String path) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(path);
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        repositoryService.createDeployment().addZipInputStream(zipInputStream).deploy();
    }

    @Override
    public void startUpProceses(ProcessFormVo processFormVo) {
        Long processTypeId = processFormVo.getProcessTypeId();
        Long processTemplateId = processFormVo.getProcessTemplateId();
        String formValues = processFormVo.getFormValues();
        Long userId = LoginUserInfoHelper.getUserId();
        // 获取当前用户
        SysUser sysUser = userService.getById(userId);
        // 获取模板
        ProcessTemplate template = templateService.getById(processTemplateId);
        // 向process表插入数据
        Process process = new Process();
        process.setUserId(userId);
        process.setStatus(1);
        process.setProcessCode(System.currentTimeMillis()+"");
        process.setProcessTemplateId(processTemplateId);
        process.setProcessTypeId(processTypeId);
        process.setFormValues(formValues);
        process.setTitle(sysUser.getName()+"发起"+template.getName()+"申请");
        baseMapper.insert(process);
        //启动流程实例
        String definitionKey = template.getProcessDefinitionKey();
        String businessKey = process.getId()+"";
        JSONObject formData = JSON.parseObject(formValues).getJSONObject("formData");
        Map<String, Object> map = new HashMap<>();
        for(Map.Entry<String, Object> entry: formData.entrySet()){
            map.put(entry.getKey(),entry.getValue());
        }
        Map<String, Object> formMap = new HashMap<>();
        formMap.put("data", map);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(definitionKey, businessKey, formMap);
        //查询当前节点审批人
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(processInstance.getId()).list();
        List<String> nameList = new ArrayList<>();
        for(Task item: taskList){
            String assignee = item.getAssignee();//获取任务的审批人
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getUsername,assignee);
            SysUser user = userService.getOne(wrapper);
            nameList.add(user.getName());
            //微信公众号推送消息
            messageService.pushPendingMessage(process.getId(),user.getId(),item.getId());
        }
        //更新process表
        process.setProcessInstanceId(processInstance.getId());
        process.setDescription("等待"+ StringUtils.join(nameList.toArray(),",") +"审批");
        baseMapper.updateById(process);
        //更新process_record表
        recordService.record(process.getId(),1,"发起申请");
    }

    @Override
    public IPage<ProcessVo> findBusiness(Page<Process> page) {
        TaskQuery taskQuery = taskService.createTaskQuery().taskAssignee(LoginUserInfoHelper.getUsername())
                .orderByTaskCreateTime().desc();
        List<Task> taskList = taskQuery.listPage((int) ((page.getCurrent() - 1) * page.getSize()), (int) page.getSize());
        long count = taskQuery.count();
        List<ProcessVo> processVoList = new ArrayList<>();
        for(Task task: taskList){
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                                                            .processInstanceId(task.getProcessInstanceId())
                                                            .singleResult();
            String businessKey = processInstance.getBusinessKey();
            if(businessKey != null){
                Process process = baseMapper.selectById(Long.parseLong(businessKey));
                if(process!=null) {
                    ProcessVo processVo = new ProcessVo();
                    BeanUtils.copyProperties(process, processVo);
                    processVo.setTaskId(task.getId());
                    processVoList.add(processVo);
                }
            }
        }
        IPage<ProcessVo> pageModel = new Page<>(page.getCurrent(),page.getSize(),count);
        pageModel.setRecords(processVoList);
        return pageModel;
    }

    @Override
    public Map<String, Object> showTaskInfo(Long processId) {
        //获取要操作的流程
        Process process = baseMapper.selectById(processId);
        //根据流程获取记录信息
        LambdaQueryWrapper<ProcessRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProcessRecord::getProcessId,processId);
        List<ProcessRecord> recordList = recordService.list(wrapper);
        //根据模板id查询模板信息
        ProcessTemplate template = templateService.getById(process.getProcessTemplateId());
        //判断当前用户是否能审批
        boolean canDo = false;
//        String name = userService.getById(LoginUserInfoHelper.getUserId()).getName();
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(process.getProcessInstanceId()).list();
        for(Task task:taskList){
            if(task.getAssignee().equals(LoginUserInfoHelper.getUsername())){
                canDo =true;
                break;
            }
        }
        Map<String ,Object> map = new HashMap<>();
        map.put("process", process);
        map.put("processRecordList", recordList);
        map.put("processTemplate",template);
        map.put("isApprove", canDo);
        return map;
    }
    //审批
    @Override
    public void approve(ApprovalVo approvalVo) {
        String taskId = approvalVo.getTaskId();
        Map<String, Object> variables = taskService.getVariables(taskId);
        if(approvalVo.getStatus()==1){
            Map<String,Object> map = new HashMap<>();
            taskService.complete(taskId, map);
        }else{
            this.refuse(taskId);
        }
        String description = approvalVo.getStatus().intValue()==1?"通过":"驳回";
        recordService.record(approvalVo.getProcessId(),approvalVo.getStatus(),description);

        Process process = baseMapper.selectById(approvalVo.getProcessId());
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(process.getProcessInstanceId()).list();
        if(CollectionUtils.isEmpty(taskList)){
            if(approvalVo.getStatus().intValue()==1){
                process.setDescription("审批完成");
                process.setStatus(2);
            }else{
                process.setStatus(-1);
                process.setDescription("审批驳回");
            }
        }else{
            List<String> assigneeList = new ArrayList<>();
            for(Task task:taskList){
                String assignee = task.getAssignee();
                LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(SysUser::getUsername,assignee);
                SysUser user = userService.getOne(wrapper);
                assigneeList.add(user.getName());
                //消息推送
                messageService.pushPendingMessage(process.getId(),user.getId(),task.getId());
            }
            process.setDescription("等待"+ StringUtils.join(assigneeList.toArray(),",") + "的处理");
            process.setStatus(1);
        }
        baseMapper.updateById(process);
    }

    @Override
    public IPage<ProcessVo> findProcessed(Page<Process> pageParam) {
        HistoricTaskInstanceQuery query = historyService.createHistoricTaskInstanceQuery()
                .taskAssignee(LoginUserInfoHelper.getUsername())
                .finished().orderByTaskCreateTime().desc();
        List<HistoricTaskInstance> historicTaskInstances = query
                .listPage((int) ((pageParam.getCurrent() - 1) * pageParam.getSize()), (int) pageParam.getSize());
        long count = query.count();
        List<ProcessVo> processVoList = new ArrayList<>();
        for(HistoricTaskInstance instance: historicTaskInstances){
            String processInstanceId = instance.getProcessInstanceId();
            LambdaQueryWrapper<Process> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Process::getProcessInstanceId,processInstanceId);
            Process process = baseMapper.selectOne(wrapper);
            ProcessVo processVo = new ProcessVo();
            BeanUtils.copyProperties(process,processVo);
            processVoList.add(processVo);
        }
        IPage<ProcessVo> pageModel = new Page<>(pageParam.getCurrent(),pageParam.getSize(),count);
        pageModel.setRecords(processVoList);
        return pageModel;
    }

    @Override
    public IPage<ProcessVo> findStarted(Page<ProcessVo> pageParam) {
        ProcessQueryVo processQueryVo = new ProcessQueryVo();
        processQueryVo.setUserId(LoginUserInfoHelper.getUserId());
        IPage<ProcessVo> pageModel = baseMapper.selectPage(pageParam, processQueryVo);
        return pageModel;
    }

    private void refuse(String taskId) {
        //1 根据任务id获取任务对象 Task
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();

        //2 获取流程定义模型 BpmnModel
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());

        //3 获取结束流向节点
        List<EndEvent> endEventList = bpmnModel.getMainProcess().findFlowElementsOfType(EndEvent.class);
        if(CollectionUtils.isEmpty(endEventList)) {
            return;
        }
        FlowNode endFlowNode = (FlowNode)endEventList.get(0);

        //4 当前流向节点
        FlowNode currentFlowNode = (FlowNode)bpmnModel.getMainProcess().getFlowElement(task.getTaskDefinitionKey());

        //  临时保存当前活动的原始方向
        List originalSequenceFlowList = new ArrayList<>();
        originalSequenceFlowList.addAll(currentFlowNode.getOutgoingFlows());
        //5 清理当前流动方向
        currentFlowNode.getOutgoingFlows().clear();
        //6 创建新流向
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlow");
        newSequenceFlow.setSourceFlowElement(currentFlowNode);
        newSequenceFlow.setTargetFlowElement(endFlowNode);
        //7 当前节点指向新方向
        List newSequenceFlowList = new ArrayList();
        newSequenceFlowList.add(newSequenceFlow);
        currentFlowNode.setOutgoingFlows(newSequenceFlowList);
        //8 完成当前任务
        taskService.complete(task.getId());
    }
}
