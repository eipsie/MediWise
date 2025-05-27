package com.wtu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("course")
public class Course {
    
    @TableId(value = "course_id", type = IdType.AUTO)
    private Integer courseId;
    
    @TableField("course_name")
    private String courseName;
    
    @TableField("course_type")
    private String courseType;
    
    @TableField("course_description")
    private String courseDescription;
    
    @TableField("teacher_id")
    private Integer teacherId;
    
    // 多对一关系：多个课程可以由一个教师教授
    @TableField(exist = false)
    private Teacher teacher;
} 