package com.wtu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wtu.entity.Patient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

/**
 * 患者Mapper接口
 */
@Mapper
public interface PatientMapper extends BaseMapper<Patient> {
    // 使用MyBatis-Plus提供的默认方法，无需额外定义

    /**
     * 统计所有患者数量
     * @return 患者总数
     */
    @Select("SELECT COUNT(*) FROM patient")
    int countAllPatients();
    
    /**
     * 按时间范围统计患者数量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 时间范围内的患者数
     */
    @Select("SELECT COUNT(*) FROM patient WHERE create_time BETWEEN #{startTime} AND #{endTime}")
    int countPatientsByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
} 