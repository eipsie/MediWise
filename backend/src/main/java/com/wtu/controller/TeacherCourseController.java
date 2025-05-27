package com.wtu.controller;

import com.wtu.entity.Course;
import com.wtu.entity.Teacher;
import com.wtu.mapper.CourseMapper;
import com.wtu.mapper.TeacherMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teacher-course")
@Tag(name = "教师课程关系接口", description = "处理教师和课程的关联查询")
public class TeacherCourseController {

    @Autowired
    private TeacherMapper teacherMapper;
    
    @Autowired
    private CourseMapper courseMapper;
    
    @GetMapping("/course-by-name")
    @Operation(summary = "根据课程名称查询课程信息和教师信息")
    public Course getCourseWithTeacherByName(@RequestParam String courseName) {
        return courseMapper.getCourseWithTeacherByName(courseName);
    }
    
    @GetMapping("/teacher-by-name")
    @Operation(summary = "根据教师姓名查询教师信息和课程列表")
    public Teacher getTeacherWithCoursesByName(@RequestParam String teacherName) {
        return teacherMapper.getTeacherWithCoursesByName(teacherName);
    }
} 