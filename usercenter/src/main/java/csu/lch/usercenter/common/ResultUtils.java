package csu.lch.usercenter.common;

/**
 * 返回封装类
 *
 * @author Administrator
 */
public class ResultUtils {

    /**
     * 成功
     *
     * @param data  返回数据
     * @return  返回成功信息
     * @param <T>   泛型
     */
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(1, data, "ok");
    }

    /**
     * 错误
     *
     * @param errorCode 错误码信息封装（错误码 + 简要描述）
     * @param description   业务错误具体描述
     * @return  错误码信息
     */
    public static BaseResponse error(ErrorCode errorCode, String description){
        return new BaseResponse(errorCode, description);
    }
}
