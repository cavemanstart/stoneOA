package com.stone.process.service.impl;

import com.stone.model.process.ProcessType;
import com.stone.process.mapper.OaProcessTypeMapper;
import com.stone.process.service.OaProcessTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 审批类型 服务实现类
 * </p>
 *
 * @author stone
 * @since 2023-10-05
 */
@Service
public class OaProcessTypeServiceImpl extends ServiceImpl<OaProcessTypeMapper, ProcessType> implements OaProcessTypeService {

}
