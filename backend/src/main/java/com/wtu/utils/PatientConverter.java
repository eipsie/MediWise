package com.wtu.utils;

import com.wtu.VO.patient.PatientVO;
import com.wtu.dto.patient.PatientCreateDTO;
import com.wtu.dto.patient.PatientUpdateDTO;
import com.wtu.entity.Doctor;
import com.wtu.entity.Patient;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 患者对象转换工具类
 */
public class PatientConverter {

    private static final Map<Integer, String> GENDER_MAP = Map.of(
            0, "女",
            1, "男",
            2, "未知"
    );
    
    /**
     * 患者创建DTO转患者实体
     * @param dto 患者创建DTO
     * @return 患者实体
     */
    public static Patient createDto2Entity(PatientCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Patient patient = new Patient();
        patient.setName(dto.getName());
        patient.setGender(dto.getGender());
        patient.setBirthDate(dto.getBirthDate());
        patient.setIdCard(dto.getIdCard());
        patient.setPhone(dto.getPhone());
        patient.setAddress(dto.getAddress());
        patient.setAllergies(dto.getAllergies());
        patient.setMedicalHistory(dto.getMedicalHistory());
        
        return patient;
    }
    
    /**
     * 患者更新DTO转患者实体
     * @param dto 患者更新DTO
     * @return 患者实体
     */
    public static Patient updateDto2Entity(PatientUpdateDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Patient patient = new Patient();
        patient.setId(dto.getId());
        patient.setName(dto.getName());
        patient.setGender(dto.getGender());
        patient.setBirthDate(dto.getBirthDate());
        patient.setIdCard(dto.getIdCard());
        patient.setPhone(dto.getPhone());
        patient.setAddress(dto.getAddress());
        patient.setAllergies(dto.getAllergies());
        patient.setMedicalHistory(dto.getMedicalHistory());
        
        return patient;
    }
    
    /**
     * 患者实体转视图对象
     * @param patient 患者实体
     * @return 患者视图对象
     */
    public static PatientVO entity2VO(Patient patient) {
        if (patient == null) {
            return null;
        }
        
        PatientVO vo = new PatientVO();
        vo.setId(patient.getId());
        vo.setPatientNo(patient.getPatientNo());
        vo.setName(patient.getName());
        vo.setGender(patient.getGender());
        vo.setGenderText(GENDER_MAP.getOrDefault(patient.getGender(), "未知"));
        vo.setBirthDate(patient.getBirthDate());
        
        // 计算年龄
        if (patient.getBirthDate() != null) {
            vo.setAge(Period.between(patient.getBirthDate(), LocalDate.now()).getYears());
        }
        
        // 隐藏部分身份证号
        if (patient.getIdCard() != null && patient.getIdCard().length() >= 14) {
            vo.setIdCard(patient.getIdCard().substring(0, 10) + "****" + 
                    (patient.getIdCard().length() > 14 ? patient.getIdCard().substring(14) : ""));
        } else {
            vo.setIdCard(patient.getIdCard());
        }
        
        vo.setPhone(patient.getPhone());
        vo.setAddress(patient.getAddress());
        vo.setAllergies(patient.getAllergies());
        vo.setMedicalHistory(patient.getMedicalHistory());
        vo.setCreateTime(patient.getCreateTime());
        vo.setCreatorId(patient.getCreatorId());
        
        return vo;
    }
    
    /**
     * 患者实体列表转视图对象列表
     * @param patientList 患者实体列表
     * @return 患者视图对象列表
     */
    public static List<PatientVO> entityList2VOList(List<Patient> patientList) {
        if (patientList == null) {
            return new ArrayList<>();
        }
        
        return patientList.stream()
                .map(PatientConverter::entity2VO)
                .collect(Collectors.toList());
    }
    
    /**
     * 设置患者视图对象的创建医生姓名
     * @param patientVOList 患者视图对象列表
     * @param doctorMap 医生ID与医生对象的映射
     */
    public static void setCreatorName(List<PatientVO> patientVOList, Map<Long, Doctor> doctorMap) {
        if (patientVOList == null || doctorMap == null) {
            return;
        }
        
        patientVOList.forEach(vo -> {
            if (vo.getCreatorId() != null && doctorMap.containsKey(vo.getCreatorId())) {
                Doctor doctor = doctorMap.get(vo.getCreatorId());
                vo.setCreatorName(doctor.getRealName() != null ? doctor.getRealName() : doctor.getUsername());
            }
        });
    }
} 