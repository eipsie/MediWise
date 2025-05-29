package com.wtu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wtu.entity.Doctor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AuthMapper extends BaseMapper<Doctor> {
    
    /**
     * 根据用户名查询医生信息
     * @param username 用户名
     * @return 医生信息
     */
    Doctor getDoctorByUsername(@Param("username") String username);

    /**
     * 注册新医生
     * @param doctor 医生信息对象
     */
    void register(Doctor doctor);
}