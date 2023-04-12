package csu.lch.usercenter.service;

import csu.lch.usercenter.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author Administrator
* @description 针对表【user】的数据库操作Service
* @createDate 2023-03-12 20:51:35
*/
public interface UserService extends IService<User> {

    /**
     *用户注册
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 检验密码
     * @return 新用户id
     */
    Long userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     *用户登录
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @return  用户id
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     *用户查询
     *
     * @param username  用户名
     * @return  用户信息（脱敏）
     */
    List<User> searchUserList(String username);

    /**
     * 删除用户
     *
     * @param id    用户id
     * @return  用户列表
     */
    boolean deleteUser(Long id);

    /**
     * 用户信息脱敏
     *
     * @param user  用户脱敏前的信息
     * @return  用户脱敏后的信息
     */
    User getSafeUser(User user);

    /**
     * 用户注销
     *
     * @param httpServletRequest 用户注销请求
     * @return  用户注销标志（-1）
     */
    int userLogout(HttpServletRequest httpServletRequest);
}
