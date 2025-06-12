package com.wtu.VO.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * API统一返回结果
 *
 * @param <T> 数据类型
 */
@Data
@Schema(description = "API统一返回结果")
public class ApiResult<T> {

    @Schema(description = "状态码", example = "200")
    private Integer code;

    @Schema(description = "返回消息", example = "操作成功")
    private String message;

    @Schema(description = "返回数据")
    private T data;

    @Schema(description = "是否成功", example = "true")
    private Boolean success;

    /**
     * 成功返回结果
     *
     * @param <T> 数据类型
     * @return API结果
     */
    public static <T> ApiResult<T> success() {
        return success(null);
    }

    /**
     * 成功返回结果
     *
     * @param data 返回数据
     * @param <T>  数据类型
     * @return API结果
     */
    public static <T> ApiResult<T> success(T data) {
        return success(200, "操作成功", data);
    }

    /**
     * 成功返回结果
     *
     * @param code    状态码
     * @param message 返回消息
     * @param data    返回数据
     * @param <T>     数据类型
     * @return API结果
     */
    public static <T> ApiResult<T> success(Integer code, String message, T data) {
        ApiResult<T> result = new ApiResult<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        result.setSuccess(true);
        return result;
    }

    /**
     * 失败返回结果
     *
     * @param <T> 数据类型
     * @return API结果
     */
    public static <T> ApiResult<T> fail() {
        return fail(500, "操作失败");
    }

    /**
     * 失败返回结果
     *
     * @param message 返回消息
     * @param <T>     数据类型
     * @return API结果
     */
    public static <T> ApiResult<T> fail(String message) {
        return fail(500, message);
    }

    /**
     * 失败返回结果
     *
     * @param code    状态码
     * @param message 返回消息
     * @param <T>     数据类型
     * @return API结果
     */
    public static <T> ApiResult<T> fail(Integer code, String message) {
        return fail(code, message, null);
    }

    /**
     * 失败返回结果
     *
     * @param code    状态码
     * @param message 返回消息
     * @param data    返回数据
     * @param <T>     数据类型
     * @return API结果
     */
    public static <T> ApiResult<T> fail(Integer code, String message, T data) {
        ApiResult<T> result = new ApiResult<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        result.setSuccess(false);
        return result;
    }
} 