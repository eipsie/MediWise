package com.wtu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wtu.VO.diagnosis.DiagnosisVO;
import com.wtu.annotation.DoctorOnly;
import com.wtu.dto.diagnosis.CreateDiagnosisDTO;
import com.wtu.dto.diagnosis.DiagnosisAnalyzeDTO;
import com.wtu.dto.diagnosis.UpdateDiagnosisDTO;
import com.wtu.result.Result;
import com.wtu.service.DiagnosisService;
import com.wtu.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 诊断记录控制器
 */
@RestController
@RequestMapping("/api/diagnoses")
@RequiredArgsConstructor
@Tag(name = "诊断管理", description = "诊断记录相关接口")
public class DiagnosisController {

    private final DiagnosisService diagnosisService;

    @PostMapping
    @Operation(summary = "创建诊断记录", description = "创建新的诊断记录")
    @DoctorOnly
    public Result<DiagnosisVO> createDiagnosis(@RequestBody @Valid CreateDiagnosisDTO createDTO) {
        Long doctorId = SecurityUtils.getCurrentUserId();
        DiagnosisVO result = diagnosisService.createDiagnosis(createDTO, doctorId);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取诊断记录详情", description = "根据ID获取诊断记录详情")
    @DoctorOnly
    public Result<DiagnosisVO> getDiagnosis(
            @Parameter(description = "诊断记录ID") @PathVariable Long id) {
        DiagnosisVO result = diagnosisService.getDiagnosis(id);
        return Result.success(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新诊断记录", description = "更新诊断记录信息")
    @DoctorOnly
    public Result<DiagnosisVO> updateDiagnosis(
            @Parameter(description = "诊断记录ID") @PathVariable Long id,
            @RequestBody @Valid UpdateDiagnosisDTO updateDTO) {
        Long doctorId = SecurityUtils.getCurrentUserId();
        DiagnosisVO result = diagnosisService.updateDiagnosis(id, updateDTO, doctorId);
        return Result.success(result);
    }

    @PostMapping("/analyze")
    @Operation(summary = "AI分析诊断", description = "使用AI分析诊断记录")
    @DoctorOnly
    public Result<DiagnosisVO> analyzeDiagnosis(@RequestBody @Valid DiagnosisAnalyzeDTO analyzeDTO) {
        DiagnosisVO result = diagnosisService.analyzeDiagnosis(analyzeDTO);
        return Result.success(result);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "获取患者诊断历史", description = "获取指定患者的诊断记录历史")
    @DoctorOnly
    public Result<Page<DiagnosisVO>> getPatientDiagnoses(
            @Parameter(description = "患者ID") @PathVariable Long patientId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Page<DiagnosisVO> result = diagnosisService.getPatientDiagnoses(patientId, page, size);
        return Result.success(result);
    }

    @GetMapping
    @Operation(summary = "分页查询诊断记录", description = "分页查询诊断记录列表")
    @PreAuthorize("hasRole('DOCTOR')")
    public Result<Page<DiagnosisVO>> listDiagnoses(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "关键字") @RequestParam(required = false) String keyword,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {
        Page<DiagnosisVO> result = diagnosisService.listDiagnoses(page, size, keyword, status);
        return Result.success(result);
    }
} 