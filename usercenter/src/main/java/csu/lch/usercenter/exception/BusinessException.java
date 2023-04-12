package csu.lch.usercenter.exception;

import csu.lch.usercenter.common.ErrorCode;
import lombok.Data;

/**
 * 自定义异常类
 *
 * @author Administrator
 */
@Data
public class BusinessException extends RuntimeException{

    private final int code;     //错误码   //todo 为什么要用final？
    private final String description;   //详细描述

    /**
     * 自定义异常
     *
     * @param errorCode     错误码信息封装（错误码 + 简要描述）
     * @param description   详细描述
     */
    public BusinessException(ErrorCode errorCode, String description){
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }
}
