package com.stone.process.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stone.common.result.Result;
import com.stone.model.process.ProcessType;
import com.stone.process.service.OaProcessTypeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Wrapper;
import java.util.List;

/**
 * <p>
 * 审批类型 前端控制器
 * </p>
 *
 * @author stone
 * @since 2023-10-05
 */
@RestController
@RequestMapping("/admin/process/processType")
@Api(tags = "审批类型管理")
public class OaProcessTypeController {
    @Autowired
    private OaProcessTypeService service;
    //查询所有审批分类
    @GetMapping("findAll")
    public Result findAll() {
        List<ProcessType> list = service.list();
        return Result.ok(list);
    }
    @ApiOperation("审批类型分页查询")
    @GetMapping("getPages/{current}/{size}")
    public Result getPages(@PathVariable Long current, @PathVariable Long size){
        Page<ProcessType> page = new Page<>(current,size);
        IPage<ProcessType> processTypePage = service.page(page);
        return Result.ok(processTypePage);
    }
    @ApiOperation(value = "根据id获取审批类型")
    @GetMapping("getProcessType/{id}")
    public Result get(@PathVariable Long id) {
        ProcessType processType = service.getById(id);
        return Result.ok(processType);
    }

    @ApiOperation(value = "新增")
    @PreAuthorize("hasAuthority('bnt.processType.add')")
    @PostMapping("save")
    public Result save(@RequestBody ProcessType processType) {
        service.save(processType);
        return Result.ok();
    }

    @ApiOperation(value = "修改")
    @PreAuthorize("hasAuthority('bnt.processType.update')")
    @PutMapping("update")
    public Result updateById(@RequestBody ProcessType processType) {
        service.updateById(processType);
        return Result.ok();
    }

    @ApiOperation(value = "删除")
    @PreAuthorize("hasAuthority('bnt.processType.remove')")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        service.removeById(id);
        return Result.ok();
    }
}

