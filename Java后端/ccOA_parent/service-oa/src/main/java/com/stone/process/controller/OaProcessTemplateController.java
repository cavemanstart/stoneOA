package com.stone.process.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stone.common.result.Result;
import com.stone.model.process.ProcessTemplate;
import com.stone.model.process.ProcessType;
import com.stone.process.service.OaProcessService;
import com.stone.process.service.OaProcessTemplateService;
import com.stone.process.service.OaProcessTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.ws.Service;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 审批模板 前端控制器
 * </p>
 *
 * @author stone
 * @since 2023-10-05
 */
@Api("审批模板")
@RestController
@RequestMapping("/admin/process/processTemplate")
public class OaProcessTemplateController {
    @Autowired
    private OaProcessTemplateService templateService;
    @Autowired
    private OaProcessTypeService typeService;
    @ApiOperation("审批模板分页查询")
    @GetMapping("getPages/{current}/{size}")
    public Result getPages(@PathVariable Long current, @PathVariable Long size){
        Page<ProcessTemplate> page = new Page<>(current,size);
        IPage<ProcessTemplate> pageModel = templateService.page(page);
        List<ProcessTemplate> records = pageModel.getRecords();
        for(ProcessTemplate item : records){
            ProcessType processType = typeService.getById(item.getProcessTypeId());
            if(processType!=null)
                item.setProcessTypeName(processType.getName());
        }
        return Result.ok(pageModel);
    }
    @ApiOperation(value = "获取")
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        ProcessTemplate processTemplate = templateService.getById(id);
        return Result.ok(processTemplate);
    }

    @PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "新增")
    @PostMapping("save")
    public Result save(@RequestBody ProcessTemplate processTemplate) {
        templateService.save(processTemplate);
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('bnt.processTemplate.templateSet')")
    @ApiOperation(value = "修改")
    @PutMapping("update")
    public Result updateById(@RequestBody ProcessTemplate processTemplate) {
        templateService.updateById(processTemplate);
        return Result.ok();
    }

    @PreAuthorize("hasAuthority('bnt.processTemplate.remove')")
    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        templateService.removeById(id);
        return Result.ok();
    }
    @ApiOperation("上传流程定义文件")
    @PostMapping("/uploadProcessDefinition")
    public Result uploadProcessDefinition(MultipartFile file) throws FileNotFoundException {
        //获取classes目录位置
        String path = new File(ResourceUtils.getURL("classpath:")
                .getPath()).getAbsolutePath();
        //设置上传文件夹
        File tempFile = new File(path + "/processes/");
        if(!tempFile.exists()) {
            tempFile.mkdirs();
        }
        //创建空文件，实现文件写入
        String filename = file.getOriginalFilename();
        File zipFile = new File(path + "/processes/" + filename);

        //保存文件
        try {
            file.transferTo(zipFile);
        } catch (IOException e) {
            return Result.fail();
        }

        Map<String, Object> map = new HashMap<>();
        //根据上传地址后续部署流程定义，文件名称为流程定义的默认key
        map.put("processDefinitionPath", "processes/" + filename);
        map.put("processDefinitionKey", filename.substring(0, filename.lastIndexOf(".zip")));
        return Result.ok(map);
    }

    @ApiOperation("发布审批模板")
    @PreAuthorize("hasAuthority('bnt.processTemplate.publish')")
    @GetMapping("/publish/{id}")
    public Result publish(@PathVariable Long id){
        boolean published = templateService.publish(id);
        return published?Result.ok():Result.fail().message("发布失败");
    }
}

