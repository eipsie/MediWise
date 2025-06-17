package com.wtu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wtu.entity.DiagnosisRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

/**
 * 诊断记录Mapper接口
 */
@Mapper
public interface DiagnosisMapper extends BaseMapper<DiagnosisRecord> {

    /**
     * 统计所有诊断记录数量
     * @return 诊断记录总数
     */
    @Select("SELECT COUNT(*) FROM diagnosis_record")
    int countAllDiagnoses();
    
    /**
     * 按时间范围统计诊断记录数量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 时间范围内的诊断记录数
     */
    @Select("SELECT COUNT(*) FROM diagnosis_record WHERE create_time BETWEEN #{startTime} AND #{endTime}")
    int countDiagnosesByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
} 