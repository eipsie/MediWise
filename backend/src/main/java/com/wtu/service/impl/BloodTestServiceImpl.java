package com.wtu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wtu.VO.bloodtest.BloodTestVO;
import com.wtu.dto.ai.GlmChatDTO;
import com.wtu.dto.bloodtest.BloodTestAnalyzeDTO;
import com.wtu.dto.bloodtest.CreateBloodTestDTO;
import com.wtu.entity.BloodTest;
import com.wtu.entity.Patient;
import com.wtu.exception.ResourceNotFoundException;
import com.wtu.mapper.BloodTestMapper;
import com.wtu.mapper.PatientMapper;
import com.wtu.service.BloodTestService;
import com.wtu.service.GlmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 血常规检查服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BloodTestServiceImpl extends ServiceImpl<BloodTestMapper, BloodTest> implements BloodTestService {

    private final PatientMapper patientMapper;
    private final GlmService glmService;

    @Override
    @Transactional
    public BloodTestVO createBloodTest(CreateBloodTestDTO createDTO) {
        // 验证患者是否存在
        Patient patient = patientMapper.selectById(createDTO.getPatientId());
        if (patient == null) {
            throw new ResourceNotFoundException("患者不存在");
        }
        
        // 创建血常规检查记录
        BloodTest bloodTest = new BloodTest();
        BeanUtils.copyProperties(createDTO, bloodTest);
        bloodTest.setCreateTime(LocalDateTime.now());
        bloodTest.setUpdateTime(LocalDateTime.now());
        
        // 保存血常规检查记录
        save(bloodTest);
        
        // 返回血常规检查VO
        return convertToVO(bloodTest, patient.getName());
    }

    @Override
    public BloodTestVO getBloodTest(Long id) {
        BloodTest bloodTest = getById(id);
        if (bloodTest == null) {
            throw new ResourceNotFoundException("血常规检查记录不存在");
        }
        
        Patient patient = patientMapper.selectById(bloodTest.getPatientId());
        
        return convertToVO(bloodTest, patient != null ? patient.getName() : null);
    }

    @Override
    @Transactional
    public boolean deleteBloodTest(Long id) {
        return removeById(id);
    }

    @Override
    @Transactional
    public BloodTestVO analyzeBloodTest(BloodTestAnalyzeDTO analyzeDTO) {
        BloodTest bloodTest = getById(analyzeDTO.getBloodTestId());
        if (bloodTest == null) {
            throw new ResourceNotFoundException("血常规检查记录不存在");
        }
        
        // 构建请求数据
        String promptText = buildAnalysisPrompt(bloodTest, analyzeDTO.getApplyMedicalPrompt());
        
        // 异步调用GLM大模型
        CompletableFuture.runAsync(() -> {
            try {
                // 调用大模型服务
                GlmChatDTO chatDTO = new GlmChatDTO("glm-4", promptText);
                String response = glmService.chat(chatDTO).getContent();
                
                // 将响应内容转换为JSON格式
                com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                java.util.Map<String, String> responseMap = new java.util.HashMap<>();
                responseMap.put("content", response);
                String jsonResponse = objectMapper.writeValueAsString(responseMap);
                
                // 更新血常规检查记录
                bloodTest.setAiAnalysisResult(jsonResponse);
                bloodTest.setUpdateTime(LocalDateTime.now());
                updateById(bloodTest);
                
                log.info("血常规检查记录{}的AI分析已完成", bloodTest.getId());
            } catch (Exception e) {
                log.error("血常规检查记录{}的AI分析失败", bloodTest.getId(), e);
            }
        });
        
        // 获取患者信息
        Patient patient = patientMapper.selectById(bloodTest.getPatientId());
        
        return convertToVO(bloodTest, patient != null ? patient.getName() : null);
    }

    @Override
    public Page<BloodTestVO> getPatientBloodTests(Long patientId, int page, int size) {
        // 验证患者是否存在
        Patient patient = patientMapper.selectById(patientId);
        if (patient == null) {
            throw new ResourceNotFoundException("患者不存在");
        }
        
        // 创建分页对象
        Page<BloodTest> recordPage = new Page<>(page, size);
        
        // 使用MyBatis-Plus的LambdaQueryWrapper查询
        LambdaQueryWrapper<BloodTest> queryWrapper = Wrappers.<BloodTest>lambdaQuery()
                .eq(BloodTest::getPatientId, patientId)
                .orderByDesc(BloodTest::getTestDate);
        
        // 执行查询
        page(recordPage, queryWrapper);
        
        return convertToVOPage(recordPage, patient.getName());
    }

    @Override
    public Page<BloodTestVO> listBloodTests(int page, int size, String keyword) {
        Page<BloodTest> recordPage = new Page<>(page, size);
        
        LambdaQueryWrapper<BloodTest> queryWrapper = Wrappers.<BloodTest>lambdaQuery();
        
        // 添加关键字查询条件
        if (StringUtils.hasText(keyword)) {
            // 由于关键字可能是患者姓名，需要先查询患者ID
            LambdaQueryWrapper<Patient> patientQueryWrapper = Wrappers.<Patient>lambdaQuery()
                    .like(Patient::getName, keyword);
            List<Patient> patients = patientMapper.selectList(patientQueryWrapper);
            
            if (!patients.isEmpty()) {
                List<Long> patientIds = new ArrayList<>();
                for (Patient patient : patients) {
                    patientIds.add(patient.getId());
                }
                queryWrapper.in(BloodTest::getPatientId, patientIds);
            } else {
                // 如果没有匹配的患者，返回空结果
                return new Page<>(page, size);
            }
        }
        
        // 按检测日期降序排序
        queryWrapper.orderByDesc(BloodTest::getTestDate);
        
        // 执行查询
        page(recordPage, queryWrapper);
        
        // 转换为VO并设置患者姓名
        return convertToVOPage(recordPage, null);
    }
    
    /**
     * 构建分析提示词
     */
    private String buildAnalysisPrompt(BloodTest bloodTest, Boolean applyMedicalPrompt) {
        StringBuilder prompt = new StringBuilder();
        
        // 添加医学专业提示词
        if (applyMedicalPrompt) {
            prompt.append("你是一位经验丰富的血液科医生，精通血常规检查结果的分析和解读。请根据以下血常规检查数据，提供专业的分析和建议。\n\n");
        }
        
        prompt.append("血常规检查数据：\n");
        prompt.append("- 白细胞计数(WBC): ").append(bloodTest.getWbc()).append(" × 10^9/L\n");
        prompt.append("- 红细胞计数(RBC): ").append(bloodTest.getRbc()).append(" × 10^12/L\n");
        prompt.append("- 血红蛋白(HGB): ").append(bloodTest.getHgb()).append(" g/L\n");
        prompt.append("- 血小板计数(PLT): ").append(bloodTest.getPlt()).append(" × 10^9/L\n");
        prompt.append("- 中性粒细胞百分比(NEUT%): ").append(bloodTest.getNeutp()).append("%\n");
        prompt.append("- 淋巴细胞百分比(LYM%): ").append(bloodTest.getLymp()).append("%\n\n");
        
        prompt.append("请提供以下内容：\n");
        prompt.append("1. 各项指标是否在正常范围内的分析\n");
        prompt.append("2. 可能的健康问题或疾病风险提示\n");
        prompt.append("3. 针对异常指标的建议和后续检查建议\n");
        prompt.append("4. 总体健康状况评估\n");
        
        return prompt.toString();
    }
    
    /**
     * 将实体转换为VO
     */
    private BloodTestVO convertToVO(BloodTest bloodTest, String patientName) {
        BloodTestVO vo = new BloodTestVO();
        BeanUtils.copyProperties(bloodTest, vo);
        vo.setPatientName(patientName);
        return vo;
    }
    
    /**
     * 将分页结果转换为VO分页
     */
    private Page<BloodTestVO> convertToVOPage(Page<BloodTest> recordPage, String defaultPatientName) {
        Page<BloodTestVO> voPage = new Page<>(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal());
        
        List<BloodTestVO> voList = new ArrayList<>();
        for (BloodTest record : recordPage.getRecords()) {
            String patientName = defaultPatientName;
            if (patientName == null) {
                Patient patient = patientMapper.selectById(record.getPatientId());
                patientName = patient != null ? patient.getName() : null;
            }
            voList.add(convertToVO(record, patientName));
        }
        
        voPage.setRecords(voList);
        return voPage;
    }
} 