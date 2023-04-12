package csu.lch.usercenter.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询用户请求体
 *
 * @author Administrator
 */

@Data
public class SearchUserListRequest implements Serializable {

    private static final long serialVersionUID = 6121482464221971033L;
    //用户名
    private String username;
}
