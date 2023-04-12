package csu.lch.usercenter.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除用户请求体
 *
 * @author Administrator
 */
@Data
public class DeleteUserRequest implements Serializable {

    private static final long serialVersionUID = 6375239965942222235L;
    //用户id
    private Long userId;
}
