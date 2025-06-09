package com.wtu.dto.patient;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 患者查询DTO
 */
@Data
@Schema(description = "患者查询请求")
public class PatientQueryDTO {
    
    /**
     * 当前页码
     */
    @Schema(description = "当前页码", example = "1", defaultValue = "1")
    private Integer current = 1;
    
    /**
     * 每页大小
     */
    @Schema(description = "每页大小", example = "10", defaultValue = "10")
    private Integer size = 10;
    
    /**
     * 患者姓名
     */
    @Schema(description = "患者姓名", example = "张")
    private String name;
    
    /**
     * 身份证号
     */
    @Schema(description = "身份证号", example = "110101")
    private String idCard;
    
    /**
     * 电话号码
     */
    @Schema(description = "电话号码", example = "138")
    private String phone;
} 