package csu.lch.usercenter.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 * 
 * @param <T>
 * @author Administrator 
 */
@Data
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = 6108959850790263586L;
    private int code;   //状态码
    private T data;     //数据
    private String message;     //简要描述
    private String description;     //详细描述

    /**
     * 成功通用返回
     *
     * @param code  正确码
     * @param data  数据
     * @param message   简要描述
     */
    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    /**
     * 错误通用返回
     *
     * @param errorCode     错误码信息封装（错误码 + 简要描述）
     * @param description   业务错误具体描述
     */
    public BaseResponse(ErrorCode errorCode, String description){
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.description = description;
    }
}
