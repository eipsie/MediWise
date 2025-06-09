package com.wtu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wtu.entity.Doctor;
import org.apache.ibatis.annotations.Mapper;

/**
 * 医生认证相关Mapper接口
 */
@Mapper
public interface AuthMapper extends BaseMapper<Doctor> {
    // 完全使用MyBatis-Plus提供的方法，不再需要自定义方法
}