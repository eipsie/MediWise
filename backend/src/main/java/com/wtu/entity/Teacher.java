package com.wtu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.List;

@Data
@TableName("teacher")
public class Teacher {
    
    @TableId(value = "teacher_id", type = IdType.AUTO)
    private Integer teacherId;
    
    @TableField("teacher_name")
    private String teacherName;
    
    @TableField("teacher_age")
    private Integer teacherAge;
    
    @TableField("teacher_description")
    private String teacherDescription;
    
    // 一对多关系：一个教师可以教授多门课程
    @TableField(exist = false)
    private List<Course> courses;
} 