package com.wtu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wtu.entity.Doctor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

/**
 * 医生Mapper接口
 */
@Mapper
public interface DoctorMapper extends BaseMapper<Doctor> {
    /**
     * 统计所有医生数量
     * @return 医生总数
     */
    @Select("SELECT COUNT(*) FROM doctor")
    int countAllDoctors();
    
    /**
     * 按时间范围统计医生数量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 时间范围内的医生数
     */
    @Select("SELECT COUNT(*) FROM doctor WHERE create_time BETWEEN #{startTime} AND #{endTime}")
    int countDoctorsByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
} 