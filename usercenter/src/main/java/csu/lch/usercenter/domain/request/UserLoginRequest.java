package csu.lch.usercenter.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 *
 * @author Administrator
 */
@Data
public class UserLoginRequest implements Serializable {
    //序列化id
    private static final long serialVersionUID = -2355342340452800019L;
    //用户账号
    private String userAccount;
    //用户密码
    private String userPassword;
}
