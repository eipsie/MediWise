package com.wtu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wtu.entity.SystemLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

/**
 * 系统日志Mapper接口
 */
@Mapper
public interface SystemLogMapper extends BaseMapper<SystemLog> {

    /**
     * 统计所有日志数量
     * @return 日志总数
     */
    @Select("SELECT COUNT(*) FROM system_log")
    int countAllLogs();
    
    /**
     * 按时间范围统计日志数量
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 时间范围内的日志数
     */
    @Select("SELECT COUNT(*) FROM system_log WHERE log_time BETWEEN #{startTime} AND #{endTime}")
    int countLogsByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    /**
     * 按日志类型统计数量
     * @param logType 日志类型
     * @return 指定类型的日志数量
     */
    @Select("SELECT COUNT(*) FROM system_log WHERE log_type = #{logType}")
    int countLogsByType(@Param("logType") String logType);
    
    /**
     * 按日志类型和时间范围统计数量
     * @param logType 日志类型
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 指定类型和时间范围的日志数量
     */
    @Select("SELECT COUNT(*) FROM system_log WHERE log_type = #{logType} AND log_time BETWEEN #{startTime} AND #{endTime}")
    int countLogsByTypeAndTimeRange(@Param("logType") String logType, 
                                   @Param("startTime") LocalDateTime startTime, 
                                   @Param("endTime") LocalDateTime endTime);
}
