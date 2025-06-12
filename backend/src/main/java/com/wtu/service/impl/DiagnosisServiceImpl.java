package com.wtu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wtu.VO.diagnosis.DiagnosisVO;
import com.wtu.constant.DiagnosisStatus;
import com.wtu.dto.diagnosis.CreateDiagnosisDTO;
import com.wtu.dto.diagnosis.DiagnosisAnalyzeDTO;
import com.wtu.dto.diagnosis.UpdateDiagnosisDTO;
import com.wtu.entity.DiagnosisRecord;
import com.wtu.entity.Doctor;
import com.wtu.entity.Patient;
import com.wtu.exception.ResourceNotFoundException;
import com.wtu.mapper.DiagnosisRecordMapper;
import com.wtu.mapper.DoctorMapper;
import com.wtu.mapper.PatientMapper;
import com.wtu.service.DiagnosisService;
import com.wtu.service.GlmChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 诊断服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DiagnosisServiceImpl extends ServiceImpl<DiagnosisRecordMapper, DiagnosisRecord> implements DiagnosisService {

    private final PatientMapper patientMapper;
    private final DoctorMapper doctorMapper;
    private final GlmChatService glmChatService;

    @Override
    @Transactional
    public DiagnosisVO createDiagnosis(CreateDiagnosisDTO createDTO, Long doctorId) {
        // 验证患者是否存在
        Patient patient = patientMapper.selectById(createDTO.getPatientId());
        if (patient == null) {
            throw new ResourceNotFoundException("患者不存在");
        }

        // 验证医生是否存在
        Doctor doctor = doctorMapper.selectById(doctorId);
        if (doctor == null) {
            throw new ResourceNotFoundException("医生不存在");
        }

        // 创建诊断记录
        DiagnosisRecord diagnosisRecord = new DiagnosisRecord();
        diagnosisRecord.setPatientId(createDTO.getPatientId());
        diagnosisRecord.setDoctorId(doctorId);
        diagnosisRecord.setSymptomsText(createDTO.getSymptomsText());
        diagnosisRecord.setSymptomsStructured(createDTO.getSymptomsStructured());
        diagnosisRecord.setVitalSigns(createDTO.getVitalSigns());
        diagnosisRecord.setStatus(DiagnosisStatus.DRAFT);
        diagnosisRecord.setDiagnosisTime(LocalDateTime.now()); // 设置初始诊断时间
        diagnosisRecord.setCreateTime(LocalDateTime.now());
        diagnosisRecord.setUpdateTime(LocalDateTime.now());

        // 保存诊断记录
        save(diagnosisRecord);

        // 返回诊断记录VO
        return convertToVO(diagnosisRecord, patient.getName(), doctor.getRealName());
    }

    @Override
    public DiagnosisVO getDiagnosis(Long id) {
        DiagnosisRecord diagnosisRecord = getById(id);
        if (diagnosisRecord == null) {
            throw new ResourceNotFoundException("诊断记录不存在");
        }

        Patient patient = patientMapper.selectById(diagnosisRecord.getPatientId());
        Doctor doctor = doctorMapper.selectById(diagnosisRecord.getDoctorId());

        return convertToVO(diagnosisRecord, 
                patient != null ? patient.getName() : null, 
                doctor != null ? doctor.getRealName() : null);
    }

    @Override
    @Transactional
    public DiagnosisVO updateDiagnosis(Long id, UpdateDiagnosisDTO updateDTO, Long doctorId) {
        DiagnosisRecord diagnosisRecord = getById(id);
        if (diagnosisRecord == null) {
            throw new ResourceNotFoundException("诊断记录不存在");
        }

        // 验证医生权限（只有创建者或管理员可以修改）
        if (!diagnosisRecord.getDoctorId().equals(doctorId)) {
            // 这里可以添加管理员检查逻辑
            throw new RuntimeException("无权修改此诊断记录");
        }

        // 更新诊断记录
        diagnosisRecord.setFinalDiagnosis(updateDTO.getFinalDiagnosis());
        diagnosisRecord.setTreatmentPlan(updateDTO.getTreatmentPlan());
        
        if (updateDTO.getStatus() != null) {
            diagnosisRecord.setStatus(updateDTO.getStatus());
        }
        
        // 如果状态为已完成，设置诊断时间
        if (DiagnosisStatus.COMPLETED.equals(diagnosisRecord.getStatus())) {
            diagnosisRecord.setDiagnosisTime(LocalDateTime.now());
        }
        
        diagnosisRecord.setUpdateTime(LocalDateTime.now());
        updateById(diagnosisRecord);

        // 获取患者和医生信息
        Patient patient = patientMapper.selectById(diagnosisRecord.getPatientId());
        Doctor doctor = doctorMapper.selectById(diagnosisRecord.getDoctorId());

        return convertToVO(diagnosisRecord, 
                patient != null ? patient.getName() : null, 
                doctor != null ? doctor.getRealName() : null);
    }

    @Override
    @Transactional
    public DiagnosisVO analyzeDiagnosis(DiagnosisAnalyzeDTO analyzeDTO) {
        DiagnosisRecord diagnosisRecord = getById(analyzeDTO.getDiagnosisId());
        if (diagnosisRecord == null) {
            throw new ResourceNotFoundException("诊断记录不存在");
        }

        // 更新状态为分析中
        diagnosisRecord.setStatus(DiagnosisStatus.ANALYZING);
        updateById(diagnosisRecord);

        // 构建请求数据
        String promptText = buildAnalysisPrompt(diagnosisRecord, analyzeDTO.getApplyMedicalPrompt());
        diagnosisRecord.setLlmRequestData(promptText);

        // 异步调用GLM大模型
        CompletableFuture.runAsync(() -> {
            try {
                // 调用大模型服务
                String response = glmChatService.chat(promptText);
                
                // 更新诊断记录
                diagnosisRecord.setLlmResponseData(response);
                diagnosisRecord.setStatus(DiagnosisStatus.PENDING);
                diagnosisRecord.setUpdateTime(LocalDateTime.now());
                updateById(diagnosisRecord);
                
                log.info("诊断记录{}的AI分析已完成", diagnosisRecord.getId());
            } catch (Exception e) {
                log.error("诊断记录{}的AI分析失败", diagnosisRecord.getId(), e);
                // 更新状态为草稿
                diagnosisRecord.setStatus(DiagnosisStatus.DRAFT);
                diagnosisRecord.setUpdateTime(LocalDateTime.now());
                updateById(diagnosisRecord);
            }
        });

        // 获取患者和医生信息
        Patient patient = patientMapper.selectById(diagnosisRecord.getPatientId());
        Doctor doctor = doctorMapper.selectById(diagnosisRecord.getDoctorId());

        return convertToVO(diagnosisRecord, 
                patient != null ? patient.getName() : null, 
                doctor != null ? doctor.getRealName() : null);
    }

    @Override
    public Page<DiagnosisVO> getPatientDiagnoses(Long patientId, int page, int size) {
        // 验证患者是否存在
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null) {
            throw new ResourceNotFoundException("患者不存在");
        }

        // 创建分页对象
        Page<DiagnosisRecord> recordPage = new Page<>(page, size);
        
        // 使用MyBatis-Plus的LambdaQueryWrapper查询
        LambdaQueryWrapper<DiagnosisRecord> queryWrapper = Wrappers.<DiagnosisRecord>lambdaQuery()
                .eq(DiagnosisRecord::getPatientId, patientId)
                .orderByDesc(DiagnosisRecord::getCreateTime);
        
        Page<DiagnosisRecord> resultPage = page(recordPage, queryWrapper);
        
        // 转换为VO
        return convertToVOPage(resultPage);
    }

    @Override
    public Page<DiagnosisVO> listDiagnoses(int page, int size, String keyword, String status) {
        // 创建分页对象
        Page<DiagnosisRecord> recordPage = new Page<>(page, size);
        
        // 使用MyBatis-Plus的LambdaQueryWrapper查询
        LambdaQueryWrapper<DiagnosisRecord> queryWrapper = Wrappers.<DiagnosisRecord>lambdaQuery()
                .like(keyword != null && !keyword.isEmpty(), DiagnosisRecord::getSymptomsText, keyword)
                .or()
                .like(keyword != null && !keyword.isEmpty(), DiagnosisRecord::getFinalDiagnosis, keyword)
                .eq(status != null && !status.isEmpty(), DiagnosisRecord::getStatus, status)
                .orderByDesc(DiagnosisRecord::getCreateTime);
        
        Page<DiagnosisRecord> resultPage = page(recordPage, queryWrapper);
        
        // 转换为VO
        return convertToVOPage(resultPage);
    }

    /**
     * 构建分析提示词
     */
    private String buildAnalysisPrompt(DiagnosisRecord record, Boolean applyMedicalPrompt) {
        StringBuilder prompt = new StringBuilder();
        
        // 添加专业医学提示词
        if (applyMedicalPrompt) {
            prompt.append("你是一位经验丰富的医学专家，请基于以下患者信息进行诊断分析。请提供可能的诊断、建议的检查和治疗方案。\n\n");
        }
        
        // 添加症状信息
        prompt.append("患者症状：").append(record.getSymptomsText()).append("\n\n");
        
        // 添加结构化症状信息
        if (record.getSymptomsStructured() != null && !record.getSymptomsStructured().isEmpty()) {
            prompt.append("结构化症状信息：").append(record.getSymptomsStructured()).append("\n\n");
        }
        
        // 添加生命体征信息
        if (record.getVitalSigns() != null && !record.getVitalSigns().isEmpty()) {
            prompt.append("生命体征：").append(record.getVitalSigns()).append("\n\n");
        }
        
        // 添加分析请求
        prompt.append("请提供以下内容：\n");
        prompt.append("1. 可能的诊断（列出最可能的3个诊断及其理由）\n");
        prompt.append("2. 建议的检查（需要进一步确认诊断的检查项目）\n");
        prompt.append("3. 治疗方案建议\n");
        
        return prompt.toString();
    }

    /**
     * 将实体转换为VO
     */
    private DiagnosisVO convertToVO(DiagnosisRecord record, String patientName, String doctorName) {
        DiagnosisVO vo = new DiagnosisVO();
        BeanUtils.copyProperties(record, vo);
        vo.setPatientName(patientName);
        vo.setDoctorName(doctorName);
        return vo;
    }

    /**
     * 将分页结果转换为VO分页结果
     */
    private Page<DiagnosisVO> convertToVOPage(Page<DiagnosisRecord> recordPage) {
        Page<DiagnosisVO> voPage = new Page<>(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal());
        
        List<DiagnosisVO> voList = new ArrayList<>();
        for (DiagnosisRecord record : recordPage.getRecords()) {
            // 获取患者和医生信息
            Patient patient = patientMapper.selectById(record.getPatientId());
            Doctor doctor = doctorMapper.selectById(record.getDoctorId());
            
            DiagnosisVO vo = convertToVO(record, 
                    patient != null ? patient.getName() : null, 
                    doctor != null ? doctor.getRealName() : null);
            voList.add(vo);
        }
        
        voPage.setRecords(voList);
        return voPage;
    }
} 