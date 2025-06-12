package com.wtu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wtu.entity.MedicalKnowledge;
import org.apache.ibatis.annotations.Mapper;

/**
 * 医学知识库Mapper接口
 */
@Mapper
public interface MedicalKnowledgeMapper extends BaseMapper<MedicalKnowledge> {
} 