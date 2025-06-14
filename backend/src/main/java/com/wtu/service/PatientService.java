package com.wtu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wtu.entity.Patient;
import com.wtu.exception.PatientValidationException;
import com.wtu.exception.ResourceNotFoundException;

import java.util.List;

/**
 * 患者服务接口
 */
public interface PatientService {
    
    /**
     * 创建新患者
     * @param patient 患者信息
     * @return 创建后的患者对象（包含ID）
     * @throws PatientValidationException 如果患者数据验证失败
     */
    Patient createPatient(Patient patient);
    
    /**
     * 更新患者信息
     * @param patient 患者信息
     * @return 更新后的患者对象
     * @throws PatientValidationException 如果患者数据验证失败
     * @throws ResourceNotFoundException 如果患者不存在
     */
    Patient updatePatient(Patient patient);
    
    /**
     * 根据ID删除患者
     * @param id 患者ID
     * @throws ResourceNotFoundException 如果患者不存在
     */
    void deletePatient(Long id);
    
    /**
     * 根据ID查询患者
     * @param id 患者ID
     * @return 患者信息
     * @throws ResourceNotFoundException 如果患者不存在
     */
    Patient getPatientById(Long id);
    
    /**
     * 根据患者编号查询患者
     * @param patientNo 患者编号
     * @return 患者信息
     * @throws PatientValidationException 如果患者编号为空
     */
    Patient getPatientByPatientNo(String patientNo);
    
    /**
     * 分页查询患者列表
     * @param page 分页参数
     * @param name 患者姓名(可选)
     * @param idCard 身份证号(可选)
     * @param phone 电话号码(可选)
     * @return 分页结果
     */
    IPage<Patient> pagePatients(Page<Patient> page, String name, String idCard, String phone);
    
    /**
     * 根据创建医生ID查询患者列表
     * @param doctorId 医生ID
     * @return 患者列表
     * @throws PatientValidationException 如果医生ID为空
     */
    List<Patient> getPatientsByDoctorId(Long doctorId);
} 