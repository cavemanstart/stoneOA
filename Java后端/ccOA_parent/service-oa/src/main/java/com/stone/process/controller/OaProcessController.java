package com.stone.process.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stone.common.result.Result;
import com.stone.process.service.OaProcessService;
import com.stone.vo.process.ProcessQueryVo;
import com.stone.vo.process.ProcessVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 审批类型 前端控制器
 * </p>
 *
 * @author stone
 * @since 2023-10-06
 */
@Api(tags = "流程管理")
@RestController
@RequestMapping("/admin/process")
public class OaProcessController {
    @Autowired
    private OaProcessService processService;
    @ApiOperation("获取分页列表")
    @GetMapping("/getPage/{current}/{size}")
    public Result getPage(@PathVariable Long current, @PathVariable Long size, ProcessQueryVo processQueryVo){
        Page<ProcessVo> page = new Page<>(current, size);
        IPage<ProcessVo> pageModel = processService.getPage(page, processQueryVo);
        return Result.ok(pageModel);
    }
}

