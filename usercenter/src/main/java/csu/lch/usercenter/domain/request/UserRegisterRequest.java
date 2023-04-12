package csu.lch.usercenter.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author Administrator
 */
@Data
public class UserRegisterRequest implements Serializable {
    //序列化id
    private static final long serialVersionUID = 1202114611235333684L;
    //用户账号
    private String userAccount;
    //用户密码
    private String userPassword;
    //校验密码
    private String checkPassword;
}
