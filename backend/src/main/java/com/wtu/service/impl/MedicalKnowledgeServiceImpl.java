package com.wtu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wtu.VO.knowledge.MedicalKnowledgeVO;
import com.wtu.dto.knowledge.CreateMedicalKnowledgeDTO;
import com.wtu.dto.knowledge.UpdateMedicalKnowledgeDTO;
import com.wtu.entity.MedicalKnowledge;
import com.wtu.exception.ResourceNotFoundException;
import com.wtu.mapper.MedicalKnowledgeMapper;
import com.wtu.service.MedicalKnowledgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 医学知识库服务实现类
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MedicalKnowledgeServiceImpl extends ServiceImpl<MedicalKnowledgeMapper, MedicalKnowledge> implements MedicalKnowledgeService {

    @Override
    @Transactional
    public MedicalKnowledgeVO createKnowledge(CreateMedicalKnowledgeDTO createDTO) {
        // 创建医学知识条目
        MedicalKnowledge knowledge = new MedicalKnowledge();
        BeanUtils.copyProperties(createDTO, knowledge);
        knowledge.setCreateTime(LocalDateTime.now());
        knowledge.setUpdateTime(LocalDateTime.now());
        
        // 处理JSON字段，确保空字符串转为有效JSON
        if (knowledge.getSymptoms() == null || knowledge.getSymptoms().trim().isEmpty()) {
            knowledge.setSymptoms("[]"); // 空数组是有效的JSON
        }
        
        // 保存医学知识条目
        save(knowledge);
        
        // 返回医学知识VO
        return convertToVO(knowledge);
    }

    @Override
    public MedicalKnowledgeVO getKnowledge(Long id) {
        MedicalKnowledge knowledge = getById(id);
        if (knowledge == null) {
            throw new ResourceNotFoundException("医学知识条目不存在");
        }
        
        return convertToVO(knowledge);
    }

    @Override
    @Transactional
    public MedicalKnowledgeVO updateKnowledge(Long id, UpdateMedicalKnowledgeDTO updateDTO) {
        MedicalKnowledge knowledge = getById(id);
        if (knowledge == null) {
            throw new ResourceNotFoundException("医学知识条目不存在");
        }
        
        // 更新字段
        if (updateDTO.getTitle() != null) {
            knowledge.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getDiseaseName() != null) {
            knowledge.setDiseaseName(updateDTO.getDiseaseName());
        }
        if (updateDTO.getDiseaseCode() != null) {
            knowledge.setDiseaseCode(updateDTO.getDiseaseCode());
        }
        if (updateDTO.getDrugName() != null) {
            knowledge.setDrugName(updateDTO.getDrugName());
        }
        if (updateDTO.getCategory() != null) {
            knowledge.setCategory(updateDTO.getCategory());
        }
        if (updateDTO.getDescription() != null) {
            knowledge.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getSymptoms() != null) {
            // 处理JSON字段，确保空字符串转为有效JSON
            if (updateDTO.getSymptoms().trim().isEmpty()) {
                knowledge.setSymptoms("[]"); // 空数组是有效的JSON
            } else {
                knowledge.setSymptoms(updateDTO.getSymptoms());
            }
        }
        if (updateDTO.getTreatmentGuide() != null) {
            knowledge.setTreatmentGuide(updateDTO.getTreatmentGuide());
        }
        if (updateDTO.getStatus() != null) {
            knowledge.setStatus(updateDTO.getStatus());
        }
        
        knowledge.setUpdateTime(LocalDateTime.now());
        updateById(knowledge);
        
        return convertToVO(knowledge);
    }

    @Override
    @Transactional
    public boolean deleteKnowledge(Long id) {
        return removeById(id);
    }

    @Override
    public Page<MedicalKnowledgeVO> listKnowledge(int page, int size, String keyword, String entryType, String category) {
        Page<MedicalKnowledge> knowledgePage = new Page<>(page, size);
        
        LambdaQueryWrapper<MedicalKnowledge> queryWrapper = Wrappers.<MedicalKnowledge>lambdaQuery();
        
        // 添加条件
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(MedicalKnowledge::getTitle, keyword)
                    .or()
                    .like(MedicalKnowledge::getDiseaseName, keyword)
                    .or()
                    .like(MedicalKnowledge::getDrugName, keyword)
                    .or()
                    .like(MedicalKnowledge::getDescription, keyword)
            );
        }
        
        if (StringUtils.hasText(entryType)) {
            queryWrapper.eq(MedicalKnowledge::getEntryType, entryType);
        }
        
        if (StringUtils.hasText(category)) {
            queryWrapper.eq(MedicalKnowledge::getCategory, category);
        }
        
        // 按更新时间降序排序
        queryWrapper.orderByDesc(MedicalKnowledge::getUpdateTime);
        
        // 执行查询
        page(knowledgePage, queryWrapper);
        
        return convertToVOPage(knowledgePage);
    }

    @Override
    public Page<MedicalKnowledgeVO> searchKnowledge(String keyword, int page, int size) {
        if (!StringUtils.hasText(keyword)) {
            // 如果关键字为空，返回空结果
            return new Page<>(page, size);
        }
        
        Page<MedicalKnowledge> knowledgePage = new Page<>(page, size);
        
        LambdaQueryWrapper<MedicalKnowledge> queryWrapper = Wrappers.<MedicalKnowledge>lambdaQuery()
                .and(wrapper -> wrapper
                        .like(MedicalKnowledge::getTitle, keyword)
                        .or()
                        .like(MedicalKnowledge::getDiseaseName, keyword)
                        .or()
                        .like(MedicalKnowledge::getDrugName, keyword)
                        .or()
                        .like(MedicalKnowledge::getDescription, keyword)
                        .or()
                        .like(MedicalKnowledge::getSymptoms, keyword)
                        .or()
                        .like(MedicalKnowledge::getTreatmentGuide, keyword)
                )
                .eq(MedicalKnowledge::getStatus, 1) // 只搜索启用的条目
                .orderByDesc(MedicalKnowledge::getUpdateTime);
        
        page(knowledgePage, queryWrapper);
        
        return convertToVOPage(knowledgePage);
    }
    
    /**
     * 将实体转换为VO
     */
    private MedicalKnowledgeVO convertToVO(MedicalKnowledge knowledge) {
        MedicalKnowledgeVO vo = new MedicalKnowledgeVO();
        BeanUtils.copyProperties(knowledge, vo);
        return vo;
    }
    
    /**
     * 将分页结果转换为VO分页
     */
    private Page<MedicalKnowledgeVO> convertToVOPage(Page<MedicalKnowledge> knowledgePage) {
        Page<MedicalKnowledgeVO> voPage = new Page<>(knowledgePage.getCurrent(), knowledgePage.getSize(), knowledgePage.getTotal());
        
        List<MedicalKnowledgeVO> voList = new ArrayList<>();
        for (MedicalKnowledge knowledge : knowledgePage.getRecords()) {
            voList.add(convertToVO(knowledge));
        }
        
        voPage.setRecords(voList);
        return voPage;
    }
} 