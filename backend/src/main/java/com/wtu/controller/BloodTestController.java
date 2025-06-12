package com.wtu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wtu.VO.bloodtest.BloodTestVO;
import com.wtu.annotation.DoctorOnly;
import com.wtu.dto.bloodtest.BloodTestAnalyzeDTO;
import com.wtu.dto.bloodtest.CreateBloodTestDTO;
import com.wtu.result.Result;
import com.wtu.service.BloodTestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 血常规检查控制器
 */
@RestController
@RequestMapping("/api/blood-tests")
@RequiredArgsConstructor
@Tag(name = "血常规检查", description = "血常规检查相关接口")
public class BloodTestController {

    private final BloodTestService bloodTestService;

    @PostMapping
    @Operation(summary = "创建血常规检查记录", description = "创建新的血常规检查记录")
    @DoctorOnly
    public Result<BloodTestVO> createBloodTest(@RequestBody @Valid CreateBloodTestDTO createDTO) {
        BloodTestVO result = bloodTestService.createBloodTest(createDTO);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取血常规检查详情", description = "根据ID获取血常规检查详情")
    @PreAuthorize("isAuthenticated()")
    public Result<BloodTestVO> getBloodTest(
            @Parameter(description = "血常规检查ID") @PathVariable Long id) {
        BloodTestVO result = bloodTestService.getBloodTest(id);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除血常规检查记录", description = "删除血常规检查记录")
    @DoctorOnly
    public Result<Boolean> deleteBloodTest(
            @Parameter(description = "血常规检查ID") @PathVariable Long id) {
        boolean result = bloodTestService.deleteBloodTest(id);
        return Result.success(result);
    }

    @PostMapping("/analyze")
    @Operation(summary = "AI分析血常规检查", description = "使用AI分析血常规检查结果")
    @DoctorOnly
    public Result<BloodTestVO> analyzeBloodTest(@RequestBody @Valid BloodTestAnalyzeDTO analyzeDTO) {
        BloodTestVO result = bloodTestService.analyzeBloodTest(analyzeDTO);
        return Result.success(result);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "获取患者血常规检查历史", description = "获取指定患者的血常规检查记录历史")
    @PreAuthorize("isAuthenticated()")
    public Result<Page<BloodTestVO>> getPatientBloodTests(
            @Parameter(description = "患者ID") @PathVariable Long patientId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Page<BloodTestVO> result = bloodTestService.getPatientBloodTests(patientId, page, size);
        return Result.success(result);
    }

    @GetMapping
    @Operation(summary = "分页查询血常规检查记录", description = "分页查询血常规检查记录列表")
    @DoctorOnly
    public Result<Page<BloodTestVO>> listBloodTests(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "关键字") @RequestParam(required = false) String keyword) {
        Page<BloodTestVO> result = bloodTestService.listBloodTests(page, size, keyword);
        return Result.success(result);
    }
} 