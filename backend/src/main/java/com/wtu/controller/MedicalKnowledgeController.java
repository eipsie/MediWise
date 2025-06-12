package com.wtu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wtu.VO.knowledge.MedicalKnowledgeVO;
import com.wtu.annotation.DoctorOnly;
import com.wtu.dto.knowledge.CreateMedicalKnowledgeDTO;
import com.wtu.dto.knowledge.UpdateMedicalKnowledgeDTO;
import com.wtu.result.Result;
import com.wtu.service.MedicalKnowledgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 医学知识库控制器
 */
@RestController
@RequestMapping("/api/medical-knowledge")
@RequiredArgsConstructor
@Tag(name = "医学知识库", description = "医学知识库相关接口")
public class MedicalKnowledgeController {

    private final MedicalKnowledgeService medicalKnowledgeService;

    @PostMapping
    @Operation(summary = "创建医学知识条目", description = "创建新的医学知识条目")
    @DoctorOnly
    public Result<MedicalKnowledgeVO> createKnowledge(@RequestBody @Valid CreateMedicalKnowledgeDTO createDTO) {
        MedicalKnowledgeVO result = medicalKnowledgeService.createKnowledge(createDTO);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取医学知识条目详情", description = "根据ID获取医学知识条目详情")
    @PreAuthorize("isAuthenticated()")
    public Result<MedicalKnowledgeVO> getKnowledge(
            @Parameter(description = "医学知识ID") @PathVariable Long id) {
        MedicalKnowledgeVO result = medicalKnowledgeService.getKnowledge(id);
        return Result.success(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新医学知识条目", description = "更新医学知识条目信息")
    @DoctorOnly
    public Result<MedicalKnowledgeVO> updateKnowledge(
            @Parameter(description = "医学知识ID") @PathVariable Long id,
            @RequestBody @Valid UpdateMedicalKnowledgeDTO updateDTO) {
        MedicalKnowledgeVO result = medicalKnowledgeService.updateKnowledge(id, updateDTO);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除医学知识条目", description = "删除医学知识条目")
    @DoctorOnly
    public Result<Boolean> deleteKnowledge(
            @Parameter(description = "医学知识ID") @PathVariable Long id) {
        boolean result = medicalKnowledgeService.deleteKnowledge(id);
        return Result.success(result);
    }

    @GetMapping
    @Operation(summary = "分页查询医学知识条目", description = "分页查询医学知识条目列表")
    @PreAuthorize("isAuthenticated()")
    public Result<Page<MedicalKnowledgeVO>> listKnowledge(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "关键字") @RequestParam(required = false) String keyword,
            @Parameter(description = "条目类型(DISEASE/DRUG)") @RequestParam(required = false) String entryType,
            @Parameter(description = "分类") @RequestParam(required = false) String category) {
        Page<MedicalKnowledgeVO> result = medicalKnowledgeService.listKnowledge(page, size, keyword, entryType, category);
        return Result.success(result);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索医学知识", description = "根据关键字搜索医学知识")
    @PreAuthorize("isAuthenticated()")
    public Result<Page<MedicalKnowledgeVO>> searchKnowledge(
            @Parameter(description = "关键字") @RequestParam String keyword,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int size) {
        Page<MedicalKnowledgeVO> result = medicalKnowledgeService.searchKnowledge(keyword, page, size);
        return Result.success(result);
    }
} 