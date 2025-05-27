package com.wtu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wtu.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
    
    /**
     * 根据课程名称查询课程信息及其对应的教师信息
     * @param courseName 课程名称
     * @return 课程信息（包含教师信息）
     */
    Course getCourseWithTeacherByName(@Param("courseName") String courseName);
} 