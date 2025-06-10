package com.wtu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wtu.VO.common.PageVO;
import com.wtu.VO.patient.PatientVO;
import com.wtu.annotation.AdminOnly;
import com.wtu.annotation.DoctorOnly;
import com.wtu.annotation.PatientCreatorOrAdmin;
import com.wtu.dto.patient.PatientCreateDTO;
import com.wtu.dto.patient.PatientQueryDTO;
import com.wtu.dto.patient.PatientUpdateDTO;
import com.wtu.entity.Doctor;
import com.wtu.entity.Patient;
import com.wtu.mapper.AuthMapper;
import com.wtu.result.Result;
import com.wtu.service.PatientService;
import com.wtu.utils.PatientConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 患者管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Tag(name = "患者管理", description = "患者信息管理相关接口")
public class PatientController {

    private final PatientService patientService;
    private final AuthMapper authMapper;
    private final HttpServletRequest request;

    /**
     * 创建患者记录
     * @param dto 患者创建数据
     * @return 创建的患者
     */
    @PostMapping
    @DoctorOnly
    @Operation(summary = "创建患者", description = "创建新的患者记录")
    public Result<PatientVO> createPatient(@RequestBody PatientCreateDTO dto) {
        // 验证参数
        if (dto == null) {
            return Result.error("请求参数不能为空");
        }
        
        // 验证必填字段
        if (!StringUtils.hasText(dto.getName())) {
            return Result.error("患者姓名不能为空");
        }
        
        // 获取当前登录医生
        Doctor currentDoctor = getCurrentDoctor();
        if (currentDoctor == null) {
            return Result.error("未获取到当前登录用户信息");
        }
        
        // 创建患者记录
        Patient patient = PatientConverter.createDto2Entity(dto);
        patient.setCreatorId(currentDoctor.getId());
        
        // 调用服务保存
        boolean success = patientService.createPatient(patient);
        if (success) {
            // 转换为VO并返回
            PatientVO vo = PatientConverter.entity2VO(patient);
            vo.setCreatorName(currentDoctor.getRealName() != null ? 
                    currentDoctor.getRealName() : currentDoctor.getUsername());
            return Result.success(vo);
        } else {
            return Result.error("创建失败");
        }
    }
    
    /**
     * 更新患者记录
     * @param id 患者ID
     * @param dto 更新数据
     * @return 更新后的患者
     */
    @PutMapping("/{id}")
    @PatientCreatorOrAdmin
    @Operation(summary = "更新患者", description = "更新指定ID的患者信息")
    public Result<PatientVO> updatePatient(
            @Parameter(description = "患者ID") @PathVariable Long id, 
            @RequestBody PatientUpdateDTO dto) {
        // 验证参数
        if (dto == null) {
            return Result.error("请求参数不能为空");
        }
        
        // 检查患者是否存在
        Patient existingPatient = patientService.getPatientById(id);
        if (existingPatient == null) {
            return Result.error("患者不存在");
        }
        
        // 获取当前登录医生
        Doctor currentDoctor = getCurrentDoctor();
        if (currentDoctor == null) {
            return Result.error("未获取到当前登录用户信息");
        }
        
        // 转换为实体并设置ID
        Patient patient = PatientConverter.updateDto2Entity(dto);
        patient.setId(id);
        
        // 调用服务更新
        boolean success = patientService.updatePatient(patient);
        if (success) {
            // 重新查询最新数据
            Patient updatedPatient = patientService.getPatientById(id);
            // 转换为VO并返回
            PatientVO vo = PatientConverter.entity2VO(updatedPatient);
            
            // 获取创建者信息
            if (vo.getCreatorId() != null) {
                Doctor doctor = getDoctor(vo.getCreatorId());
                if (doctor != null) {
                    vo.setCreatorName(doctor.getRealName() != null ? doctor.getRealName() : doctor.getUsername());
                }
            }
            
            return Result.success(vo);
        } else {
            return Result.error("更新失败");
        }
    }
    
    /**
     * 删除患者记录
     * @param id 患者ID
     * @return 结果
     */
    @DeleteMapping("/{id}")
    @PatientCreatorOrAdmin
    @Operation(summary = "删除患者", description = "删除指定ID的患者")
    public Result<Void> deletePatient(@Parameter(description = "患者ID") @PathVariable Long id) {
        // 检查患者是否存在
        Patient existingPatient = patientService.getPatientById(id);
        if (existingPatient == null) {
            return Result.error("患者不存在");
        }
        
        // 调用服务删除
        boolean success = patientService.deletePatient(id);
        if (success) {
            return Result.success();
        } else {
            return Result.error("删除失败");
        }
    }
    
    /**
     * 根据ID查询患者
     * @param id 患者ID
     * @return 患者信息
     */
    @GetMapping("/{id}")
    @PatientCreatorOrAdmin
    @Operation(summary = "查询患者", description = "根据ID查询患者详情")
    public Result<PatientVO> getPatientById(@Parameter(description = "患者ID") @PathVariable Long id) {
        Patient patient = patientService.getPatientById(id);
        if (patient != null) {
            // 转换为VO
            PatientVO vo = PatientConverter.entity2VO(patient);
            
            // 获取创建者信息
            if (vo.getCreatorId() != null) {
                Doctor doctor = getDoctor(vo.getCreatorId());
                if (doctor != null) {
                    vo.setCreatorName(doctor.getRealName() != null ? doctor.getRealName() : doctor.getUsername());
                }
            }
            
            return Result.success(vo);
        } else {
            return Result.error("患者不存在");
        }
    }
    
    /**
     * 根据患者编号查询患者
     * @param patientNo 患者编号
     * @return 患者信息
     */
    @GetMapping("/no/{patientNo}")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @Operation(summary = "根据编号查询", description = "根据患者编号查询患者详情")
    public Result<PatientVO> getPatientByPatientNo(@Parameter(description = "患者编号") @PathVariable String patientNo) {
        if (!StringUtils.hasText(patientNo)) {
            return Result.error("患者编号不能为空");
        }
        
        Patient patient = patientService.getPatientByPatientNo(patientNo);
        if (patient != null) {
            // 转换为VO
            PatientVO vo = PatientConverter.entity2VO(patient);
            
            // 获取创建者信息
            if (vo.getCreatorId() != null) {
                Doctor doctor = getDoctor(vo.getCreatorId());
                if (doctor != null) {
                    vo.setCreatorName(doctor.getRealName() != null ? doctor.getRealName() : doctor.getUsername());
                }
            }
            
            return Result.success(vo);
        } else {
            return Result.error("患者不存在");
        }
    }

    /**
     * 分页查询患者列表
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    @PostMapping("/page")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @Operation(summary = "分页查询", description = "分页查询患者列表，支持条件过滤")
    public Result<PageVO<PatientVO>> pagePatients(@RequestBody PatientQueryDTO queryDTO) {
        // 确保查询参数不为空
        if (queryDTO == null) {
            queryDTO = new PatientQueryDTO();
        }
        
        // 创建分页对象
        Page<Patient> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        IPage<Patient> pageResult = patientService.pagePatients(page, queryDTO.getName(), 
                queryDTO.getIdCard(), queryDTO.getPhone());
        
        // 转换为VO列表
        List<PatientVO> patientVOList = PatientConverter.entityList2VOList(pageResult.getRecords());
        
        // 获取所有创建者ID
        List<Long> creatorIds = patientVOList.stream()
                .map(PatientVO::getCreatorId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());
        
        // 批量查询创建者信息
        Map<Long, Doctor> doctorMap = new HashMap<>();
        if (!creatorIds.isEmpty()) {
            LambdaQueryWrapper<Doctor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(Doctor::getId, creatorIds);
            List<Doctor> doctors = authMapper.selectList(queryWrapper);
            if (doctors != null && !doctors.isEmpty()) {
                doctorMap = doctors.stream()
                        .collect(Collectors.toMap(Doctor::getId, doctor -> doctor));
            }
        }
        
        // 设置创建者姓名
        PatientConverter.setCreatorName(patientVOList, doctorMap);
        
        // 构建分页结果
        PageVO<PatientVO> pageVO = PageVO.of(
                pageResult.getTotal(), 
                pageResult.getPages(), 
                pageResult.getCurrent(), 
                pageResult.getSize(), 
                patientVOList
        );
        
        return Result.success(pageVO);
    }
    
    /**
     * GET方式分页查询患者列表
     * @param current 当前页码
     * @param size 每页大小
     * @param name 患者姓名
     * @param idCard 身份证号
     * @param phone 电话号码
     * @return 分页结果
     */
    @GetMapping("/page")
    @PreAuthorize("hasAnyRole('DOCTOR', 'ADMIN')")
    @Operation(summary = "GET方式分页查询", description = "分页查询患者列表，支持条件过滤(兼容表单提交)")
    public Result<PageVO<PatientVO>> pagePatientsByGet(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "患者姓名") @RequestParam(required = false) String name,
            @Parameter(description = "身份证号") @RequestParam(required = false) String idCard,
            @Parameter(description = "电话号码") @RequestParam(required = false) String phone) {
        
        // 创建查询DTO并设置参数
        PatientQueryDTO queryDTO = new PatientQueryDTO();
        queryDTO.setCurrent(current);
        queryDTO.setSize(size);
        queryDTO.setName(name);
        queryDTO.setIdCard(idCard);
        queryDTO.setPhone(phone);
        
        // 复用POST分页查询方法
        return pagePatients(queryDTO);
    }

    /**
     * 查询当前登录医生创建的患者列表
     * @return 患者列表
     */
    @GetMapping("/my-patients")
    @DoctorOnly
    @Operation(summary = "我的患者", description = "查询当前登录医生创建的患者列表")
    public Result<List<PatientVO>> getMyPatients() {
        // 获取当前登录用户
        Doctor currentDoctor = getCurrentDoctor();
        if (currentDoctor == null) {
            return Result.error("未获取到当前登录用户信息");
        }
        
        // 查询医生创建的患者列表
        List<Patient> patients = patientService.getPatientsByDoctorId(currentDoctor.getId());
        
        // 转换为VO列表
        List<PatientVO> patientVOList = PatientConverter.entityList2VOList(patients);
        
        // 设置创建者姓名
        patientVOList.forEach(vo -> 
            vo.setCreatorName(currentDoctor.getRealName() != null ? 
                    currentDoctor.getRealName() : currentDoctor.getUsername())
        );
        
        return Result.success(patientVOList);
    }
    
    /**
     * 获取当前登录的医生信息
     * @return 医生信息
     */
    private Doctor getCurrentDoctor() {
        // 从请求属性中获取userId (由JWT拦截器放入)
        Object userId = request.getAttribute("userId");
        if (userId != null) {
            // 从数据库查询医生信息
            return authMapper.selectById(Long.valueOf(userId.toString()));
        }
        return null;
    }
    
    /**
     * 根据ID查询医生信息
     * @param doctorId 医生ID
     * @return 医生信息
     */
    private Doctor getDoctor(Long doctorId) {
        if (doctorId == null) {
            return null;
        }
        return authMapper.selectById(doctorId);
    }
} 