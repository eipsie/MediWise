package com.wtu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wtu.VO.diagnosis.DiagnosisVO;
import com.wtu.dto.diagnosis.CreateDiagnosisDTO;
import com.wtu.dto.diagnosis.DiagnosisAnalyzeDTO;
import com.wtu.dto.diagnosis.UpdateDiagnosisDTO;

/**
 * 诊断服务接口
 */
public interface DiagnosisService {
    
    /**
     * 创建诊断记录
     *
     * @param createDTO 创建诊断记录DTO
     * @param doctorId 医生ID
     * @return 创建的诊断记录VO
     */
    DiagnosisVO createDiagnosis(CreateDiagnosisDTO createDTO, Long doctorId);
    
    /**
     * 获取诊断记录详情
     *
     * @param id 诊断记录ID
     * @return 诊断记录VO
     */
    DiagnosisVO getDiagnosis(Long id);
    
    /**
     * 更新诊断记录
     *
     * @param id 诊断记录ID
     * @param updateDTO 更新诊断记录DTO
     * @param doctorId 医生ID
     * @return 更新后的诊断记录VO
     */
    DiagnosisVO updateDiagnosis(Long id, UpdateDiagnosisDTO updateDTO, Long doctorId);
    
    /**
     * AI分析诊断
     *
     * @param analyzeDTO 诊断分析请求DTO
     * @return 包含AI分析结果的诊断记录VO
     */
    DiagnosisVO analyzeDiagnosis(DiagnosisAnalyzeDTO analyzeDTO);
    
    /**
     * 获取患者的诊断历史
     *
     * @param patientId 患者ID
     * @param page 页码
     * @param size 每页大小
     * @return 诊断记录分页结果
     */
    Page<DiagnosisVO> getPatientDiagnoses(Long patientId, int page, int size);
    
    /**
     * 分页查询诊断记录
     *
     * @param page 页码
     * @param size 每页大小
     * @param keyword 关键字
     * @param status 状态
     * @return 诊断记录分页结果
     */
    Page<DiagnosisVO> listDiagnoses(int page, int size, String keyword, String status);
} 