package com.wtu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wtu.entity.Patient;
import org.apache.ibatis.annotations.Mapper;

/**
 * 患者Mapper接口
 */
@Mapper
public interface PatientMapper extends BaseMapper<Patient> {
    // 使用MyBatis-Plus提供的默认方法，无需额外定义
} 