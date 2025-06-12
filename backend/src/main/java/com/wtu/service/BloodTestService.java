package com.wtu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wtu.VO.bloodtest.BloodTestVO;
import com.wtu.dto.bloodtest.BloodTestAnalyzeDTO;
import com.wtu.dto.bloodtest.CreateBloodTestDTO;
import com.wtu.entity.BloodTest;

/**
 * 血常规检查服务接口
 */
public interface BloodTestService extends IService<BloodTest> {
    
    /**
     * 创建血常规检查记录
     *
     * @param createDTO 创建血常规检查DTO
     * @return 创建的血常规检查VO
     */
    BloodTestVO createBloodTest(CreateBloodTestDTO createDTO);
    
    /**
     * 获取血常规检查详情
     *
     * @param id 血常规检查ID
     * @return 血常规检查VO
     */
    BloodTestVO getBloodTest(Long id);
    
    /**
     * 删除血常规检查记录
     *
     * @param id 血常规检查ID
     * @return 是否删除成功
     */
    boolean deleteBloodTest(Long id);
    
    /**
     * AI分析血常规检查
     *
     * @param analyzeDTO 血常规分析请求DTO
     * @return 包含AI分析结果的血常规检查VO
     */
    BloodTestVO analyzeBloodTest(BloodTestAnalyzeDTO analyzeDTO);
    
    /**
     * 获取患者的血常规检查历史
     *
     * @param patientId 患者ID
     * @param page 页码
     * @param size 每页大小
     * @return 血常规检查分页结果
     */
    Page<BloodTestVO> getPatientBloodTests(Long patientId, int page, int size);
    
    /**
     * 分页查询血常规检查记录
     *
     * @param page 页码
     * @param size 每页大小
     * @param keyword 关键字
     * @return 血常规检查分页结果
     */
    Page<BloodTestVO> listBloodTests(int page, int size, String keyword);
} 