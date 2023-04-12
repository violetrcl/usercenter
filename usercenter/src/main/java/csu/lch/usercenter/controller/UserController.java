package csu.lch.usercenter.controller;

import csu.lch.usercenter.common.BaseResponse;
import csu.lch.usercenter.common.ErrorCode;
import csu.lch.usercenter.common.ResultUtils;
import csu.lch.usercenter.domain.User;
import csu.lch.usercenter.domain.request.DeleteUserRequest;
import csu.lch.usercenter.domain.request.SearchUserListRequest;
import csu.lch.usercenter.domain.request.UserLoginRequest;
import csu.lch.usercenter.domain.request.UserRegisterRequest;
import csu.lch.usercenter.exception.BusinessException;
import csu.lch.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static csu.lch.usercenter.constant.UserConstant.ADMIN_ROLE;
import static csu.lch.usercenter.constant.UserConstant.USER_LOGIN_STATE;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册接口
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册账号为空");
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册账号或密码或校验密码为空");
        }
        Long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录接口
     *
     * @param userLoginRequest
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest httpServletRequest) {
        if (userLoginRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "登录请求为空");

        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if(StringUtils.isAnyBlank(userAccount, userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "登录账号或密码为空");

        }
        User result = userService.userLogin(userAccount, userPassword, httpServletRequest);
        return ResultUtils.success(result);
    }

    /**
     * 用户注销接口
     *
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest httpServletRequest) {
        if (httpServletRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注销请求为空");
        }
        Integer result = userService.userLogout(httpServletRequest);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前用户 接口
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest httpServletRequest){
        Object userObj = httpServletRequest.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN, "请先登录");
        }
        long userId = currentUser.getUserId();
        //todo 检验用户是否合法
        User user = userService.getById(userId);
        User result = userService.getSafeUser(user);
        return ResultUtils.success(result);
    }

    /**
     * 查询用户接口
     *
     * @param searchUserListRequest
     * @param httpServletRequest
     * @return
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUserList(SearchUserListRequest searchUserListRequest, HttpServletRequest httpServletRequest){
        if (searchUserListRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "查询请求为空");
        }
        if (isNotAdmin(httpServletRequest)){
            throw new BusinessException(ErrorCode.NOT_AUTH, "当前用户无访问权限");
        }

        String username = searchUserListRequest.getUsername();
//        if (StringUtils.isBlank(username)){   todo: 用户名为空抛出异常
//            return new ArrayList<>();
//        }
        List<User> result =  userService.searchUserList(username);
        return ResultUtils.success(result);
    }

    /**
     * 删除用户接口
     *
     * @param deleteUserRequest
     * @param httpServletRequest
     * @return
     */
    @DeleteMapping ("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteUserRequest deleteUserRequest, HttpServletRequest httpServletRequest){
        if (deleteUserRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "删除请求为空");
        }
        if (isNotAdmin(httpServletRequest)){
            throw new BusinessException(ErrorCode.NOT_AUTH, "当前用户无访问权限");
        }

        Long userId = deleteUserRequest.getUserId();
        if (userId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id小于0，错误id");
        }
        Boolean result = userService.deleteUser(userId);
        return ResultUtils.success(result);
    }

    /**
     * 鉴权，是否为管理员
     *
     * @param httpServletRequest 请求
     * @return 是/否
     */
    private boolean isNotAdmin(HttpServletRequest httpServletRequest){
        User user = (User)httpServletRequest.getSession().getAttribute(USER_LOGIN_STATE);
        return user == null || user.getUserRole() != ADMIN_ROLE;
    }
}
