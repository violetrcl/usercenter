package csu.lch.usercenter.common;

/**
 * 错误码信息封装
 *
 * @author Administrator
 */
public enum ErrorCode {
    SUCCESS(1,"OK"),
    PARAMS_ERROR(40000, "请求参数错误"),
    NULL_ERROR(40001, "请求数据为空"),
    NOT_LOGIN(40100, "用户未登录"),
    NOT_AUTH(40101,"无访问权限"),
    SYSTEM_ERROR(50000, "系统内部异常");

    private final int code;   //错误码
    private final String message;     //错误码的精简定义

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
