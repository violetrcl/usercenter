package csu.lch.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import csu.lch.usercenter.common.ErrorCode;
import csu.lch.usercenter.domain.User;
import csu.lch.usercenter.exception.BusinessException;
import csu.lch.usercenter.mapper.UserMapper;
import csu.lch.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static csu.lch.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author Administrator
* @description 针对表【user】的数据库操作Service实现
* @createDate 2023-03-12 20:51:35
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService {

    @Resource
    private UserMapper userMapper;

    private static final String SALT = "salt";  // “盐值”，混淆密码

    /**
     * 用户注册
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 检验密码
     * @return 注册用户id
     */
    @Override
    public Long userRegister(String userAccount, String userPassword, String checkPassword) {
        //1、校验账号密码是否合法
        //账号不能为空
        if(userAccount == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册账号为空");
        }
        //账号长度不小于4
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册账号过短");
        }
        //账号不能包含特殊字符(使用正则表达式）
        String validPattern = "/^[a-zA-Z0-9]+$/ \n";        //todo 貌似 “不能包含特殊字符” 这个功能没实现成功
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册账号带有特殊字符");
        }
        //账号不能已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = this.count(queryWrapper);
        if (count > 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册账号已存在");
        }

        //密码不能为空
        if(userPassword == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册密码为空");
        }
        //密码长度不小于8
        if(userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "注册密码过短");
        }

        //校验密码不能为空
        if (checkPassword == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "校验密码为空");
        }
        //校验密码不能与密码不同
        if (!checkPassword.equals(userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "校验密码与密码不同");
        }

        //2、加密（将密码转为密文）
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //3、插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.save(user);
        if (!saveResult){
            throw new BusinessException(ErrorCode.NULL_ERROR, "插入失败，请求数据为空");
        }
        return user.getUserId();
    }

    /**
     * 用户登录
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param request 登录请求
     * @return 脱敏后的用户信息
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1、校验账号密码是否合法
        //账号不能为空
        if(userAccount == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "登录账号为空");
        }
        //账号长度不小于4
        if(userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "登录账号过短");
        }
        //账号不能包含特殊字符(使用正则表达式）
        String validPattern = "/^[a-zA-Z0-9]+$/ \n";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "登录账号含有特殊字符");

        }

        //密码不能为空
        if(userPassword == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "登录密码为空");

        }
        //密码长度不小于8
        if(userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "登录密码过短");
        }

        //2、验证账号密码是否正确
        //加密（将密码转为密文，因为要和密文对比）
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        //查询数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null){
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号与密码不匹配");
        }

        //3、用户脱敏
        User safeUser = getSafeUser(user);

        //4、用户状态设为登录: 1
        safeUser.setUserStatus(1);

        //5、记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safeUser);

        return safeUser;    //todo 既然现在已经有了current，我认为登录的返回值可以改成true或false，前端用boolean变量接受即可
    }

    /**
     * 查询用户
     *
     * @param username 用户名
     * @return 查询到的用户列表
     */
    @Override
    public List<User> searchUserList(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)){      //todo: username为空时抛出参数错误异常
            queryWrapper.like("username", username);
        }
        List<User> userList = list(queryWrapper);
        return userList.stream().map(user -> getSafeUser(user)).collect(Collectors.toList());   //todo 理解（java8内容）
    }

    /**
     * 删除用户
     *
     * @param id 用户id
     * @return 是否删除成功
     */
    public boolean deleteUser(Long id){
        if (id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id小于0，错误id");
        }
        return removeById(id);
    }

    /**
     * 用户信息脱敏
     *
     * @param user  脱敏前的用户信息
     * @return  脱敏后的用户信息
     */ // TODO: 2023/3/15  这里的藕合好像解决不了
    @Override
    public User getSafeUser(User user){
        if(user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "预脱敏用户为空");
        }
        User safeUser = new User();
        safeUser.setUserId(user.getUserId());
        safeUser.setUsername(user.getUsername());
        safeUser.setUserAccount(user.getUserAccount());
        safeUser.setUserAvatarUrl(user.getUserAvatarUrl());
        safeUser.setUserGender(user.getUserGender());
        safeUser.setUserPhone(user.getUserPhone());
        safeUser.setUserEmail(user.getUserEmail());
        safeUser.setUserStatus(user.getUserStatus());
        safeUser.setUserRole(user.getUserRole());
        safeUser.setCreateTime(user.getCreateTime());
        safeUser.setUpdateTime(user.getUpdateTime());
        safeUser.setIsDelete(0);
        return safeUser;
    }

    /**
     * 用户注销
     *
     * @param httpServletRequest 用户注销请求
     */
    @Override
    public int userLogout(HttpServletRequest httpServletRequest) {
        httpServletRequest.getSession().removeAttribute(USER_LOGIN_STATE);      //todo 登录态和用户是一对一的吗
        return -1;
    }
}
  