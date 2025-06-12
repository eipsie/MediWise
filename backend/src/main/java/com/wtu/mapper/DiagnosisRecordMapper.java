package com.wtu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wtu.entity.DiagnosisRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 诊断记录Mapper接口
 */
@Mapper
public interface DiagnosisRecordMapper extends BaseMapper<DiagnosisRecord> {
    
    /**
     * 查询患者诊断记录
     *
     * @param page 分页对象
     * @param patientId 患者ID
     * @return 诊断记录分页结果
     */
    Page<DiagnosisRecord> selectPatientDiagnoses(Page<DiagnosisRecord> page, @Param("patientId") Long patientId);
    
    /**
     * 根据条件查询诊断记录
     *
     * @param page 分页对象
     * @param keyword 关键字
     * @param status 状态
     * @return 诊断记录分页结果
     */
    Page<DiagnosisRecord> selectDiagnoses(Page<DiagnosisRecord> page, @Param("keyword") String keyword, @Param("status") String status);
} 