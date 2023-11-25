package com.stone.process.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stone.model.process.Process;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.stone.vo.process.ProcessQueryVo;
import com.stone.vo.process.ProcessVo;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 审批类型 Mapper 接口
 * </p>
 *
 * @author stone
 * @since 2023-10-06
 */
public interface OaProcessMapper extends BaseMapper<Process> {
    IPage<ProcessVo> selectPage(Page<ProcessVo> page, @Param("vo") ProcessQueryVo processQueryVo);
}
