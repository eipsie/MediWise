package com.wtu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TeacherMapper extends BaseMapper<Teacher> {
    
    /**
     * 根据教师姓名查询教师信息及其教授的所有课程
     * @param teacherName 教师姓名
     * @return 教师信息（包含课程列表）
     */
    Teacher getTeacherWithCoursesByName(@Param("teacherName") String teacherName);
} 