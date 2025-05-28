package com.wtu.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 后端统一返回结果
 * @param <T> 返回数据类型
 */
@Data
@Schema(description = "统一返回结果")
public class Result<T> implements Serializable {

    @Schema(description = "状态码：1成功，0和其它数字为失败", example = "1")
    private Integer code; //编码：1成功，0和其它数字为失败
    
    @Schema(description = "错误信息，成功时为null", example = "用户名或密码错误")
    private String msg; //错误信息
    
    @Schema(description = "返回数据，失败时为null")
    private T data; //数据

    /**
     * 返回成功结果，无数据
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.code = 1;
        return result;
    }

    /**
     * 返回成功结果，带数据
     * @param object 返回的数据
     */
    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = 1;
        return result;
    }

    /**
     * 返回错误结果，带错误信息
     * @param msg 错误信息
     */
    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.msg = msg;
        result.code = 0;
        return result;
    }
}
