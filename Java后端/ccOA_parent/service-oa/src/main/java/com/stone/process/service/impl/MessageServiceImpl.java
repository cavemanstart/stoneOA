package com.stone.process.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.stone.auth.service.SysUserService;
import com.stone.model.process.Process;
import com.stone.model.process.ProcessTemplate;
import com.stone.model.system.SysUser;
import com.stone.process.service.MessageService;
import com.stone.process.service.OaProcessService;
import com.stone.process.service.OaProcessTemplateService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateData;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    private OaProcessService processService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private OaProcessTemplateService processTemplateService;

    @Autowired
    private WxMpService wxMpService;
    //推送待审批人员
    @Override
    public void pushPendingMessage(Long processId, Long userId, String taskId) {
        //查询流程信息
        Process process = processService.getById(processId);
        //根据userid查询要推送人信息
        SysUser sysUser = sysUserService.getById(userId);
        //查询审批模板信息
        ProcessTemplate processTemplate = processTemplateService.getById(process.getProcessTemplateId());
        //获取提交审批人的信息
        SysUser submitSysUser = sysUserService.getById(process.getUserId());

        //获取要给的消息人的openid
        String openId = sysUser.getOpenId();
        if(StringUtils.isEmpty(openId)) {
            openId = "od3H46DEQDby-3a1TOFMHXrc2F4s";
        }
        //设置消息发送信息
        WxMpTemplateMessage templateMessage = WxMpTemplateMessage.builder()
                //给谁发送消息，openid值
                .toUser(openId)
                //创建模板信息的id值
                .templateId("c4x0HxpGLG_Mmm9bRxMERtP2KInLghqJlGqa3vRX8gg")
                //点击消息，跳转的地址
                .url("http://4ar0h123.beesnat.com/#/show/" + processId + "/" + taskId)
                .build();

        JSONObject jsonObject = JSON.parseObject(process.getFormValues());
        JSONObject formShowData = jsonObject.getJSONObject("formShowData");
        StringBuffer content = new StringBuffer();
        for (Map.Entry entry : formShowData.entrySet()) {
            content.append(entry.getKey()).append("：").append(entry.getValue()).append("\n ");
        }

        //设置模板里面参数值
        templateMessage
                .addData(new WxMpTemplateData("first",
                        submitSysUser.getName()+"提交"+processTemplate.getName()+",请注意查看","#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword1", process.getProcessCode()+"\n", "#272727"));
        templateMessage.addData(new WxMpTemplateData("keyword2", new DateTime(process.getCreateTime()).toString("yyyy-MM-dd HH:mm:ss"), "#272727"));
        templateMessage.addData(new WxMpTemplateData("content", content.toString(), "#272727"));

        //调用方法发送
        try {
            String msg = wxMpService.getTemplateMsgService().sendTemplateMsg(templateMessage);
            System.out.println(msg);
        } catch (WxErrorException e) {
            throw new RuntimeException(e);
        }
    }

}
