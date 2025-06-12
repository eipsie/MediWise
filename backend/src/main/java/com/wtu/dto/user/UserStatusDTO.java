package com.wtu.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户状态更新DTO
 */
@Data
@Schema(description = "用户状态更新请求")
public class UserStatusDTO {
    
    /**
     * 状态(1:正常,0:禁用)
     */
    @NotNull(message = "状态不能为空")
    @Schema(description = "状态(1:正常,0:禁用)", example = "1", required = true)
    private Integer status;
} 