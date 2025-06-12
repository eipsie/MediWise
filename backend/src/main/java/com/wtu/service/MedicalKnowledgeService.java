package com.wtu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wtu.VO.knowledge.MedicalKnowledgeVO;
import com.wtu.dto.knowledge.CreateMedicalKnowledgeDTO;
import com.wtu.dto.knowledge.UpdateMedicalKnowledgeDTO;
import com.wtu.entity.MedicalKnowledge;

/**
 * 医学知识库服务接口
 */
public interface MedicalKnowledgeService extends IService<MedicalKnowledge> {
    
    /**
     * 创建医学知识条目
     *
     * @param createDTO 创建医学知识DTO
     * @return 创建的医学知识VO
     */
    MedicalKnowledgeVO createKnowledge(CreateMedicalKnowledgeDTO createDTO);
    
    /**
     * 获取医学知识条目详情
     *
     * @param id 医学知识ID
     * @return 医学知识VO
     */
    MedicalKnowledgeVO getKnowledge(Long id);
    
    /**
     * 更新医学知识条目
     *
     * @param id 医学知识ID
     * @param updateDTO 更新医学知识DTO
     * @return 更新后的医学知识VO
     */
    MedicalKnowledgeVO updateKnowledge(Long id, UpdateMedicalKnowledgeDTO updateDTO);
    
    /**
     * 删除医学知识条目
     *
     * @param id 医学知识ID
     * @return 是否删除成功
     */
    boolean deleteKnowledge(Long id);
    
    /**
     * 分页查询医学知识条目
     *
     * @param page 页码
     * @param size 每页大小
     * @param keyword 关键字
     * @param entryType 条目类型
     * @param category 分类
     * @return 医学知识分页结果
     */
    Page<MedicalKnowledgeVO> listKnowledge(int page, int size, String keyword, String entryType, String category);
    
    /**
     * 搜索医学知识
     *
     * @param keyword 关键字
     * @param page 页码
     * @param size 每页大小
     * @return 医学知识分页结果
     */
    Page<MedicalKnowledgeVO> searchKnowledge(String keyword, int page, int size);
} 