package csu.lch.usercenter.service;

import csu.lch.usercenter.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/*
测试结果说明 ：第一遍插入新数据，测试通过，说明除 “账号不能已存在” 功能外，其它功能都正常
 第二遍插入相同数据，测试结果为false，说明 “账号不能已存在” 功能正常
 */
@SpringBootTest
class UserServiceTest {
    @Resource
    private UserService userService;

    @Test
    public void testAddUser() {
        User user = new User();
        user.setUsername("lch");
        user.setUserAccount("lichenhua");
        user.setUserAvatarUrl("https://images.zsxq.com/FnmK9ihCBMX5EIW3vsfz5cZQBgeO?e=1682870399&token=kIxbL07-8jAj8w1n4s9zv64FuZZNEATmlU_Vm6zD:fnIz_skvQQc9abyoNuqzUNzaYmE=");
        user.setUserGender(0);
        user.setUserPassword("12345678");
        user.setUserPhone("123");
        user.setUserEmail("456");

        boolean result = userService.save(user);
        System.out.println(user.getUserId());
        Assertions.assertTrue(result);
    }

    @Test
    public void userRegister(){
        //账号不能为空
        String userAccount = "";
        String userPassword = "12345678";
        String checkPassword = "12345678";
        long result = userService.userRegister(userAccount,userPassword,checkPassword);
        Assertions.assertEquals(-1,result);
        //账号长度不小于4
        userAccount = "li";
        result = userService.userRegister(userAccount,userPassword,checkPassword);
        Assertions.assertEquals(-1,result);
        //账号不能包含特殊字符
        userAccount = "lch+rcl";
        result = userService.userRegister(userAccount,userPassword,checkPassword);
        Assertions.assertEquals(-1,result);

        userAccount = "lchRcl2";

        //密码不能为空
        userPassword = "";
        result = userService.userRegister(userAccount,userPassword,checkPassword);
        Assertions.assertEquals(-1,result);
        //密码长度不小于8
        userPassword = "123456";
        checkPassword = "123456";
        result = userService.userRegister(userAccount,userPassword,checkPassword);
        Assertions.assertEquals(-1,result);

        //校验密码不能为空
        userPassword = "12345678";
        checkPassword = "";
        result = userService.userRegister(userAccount,userPassword,checkPassword);
        Assertions.assertEquals(-1,result);
        //校验密码不能与密码不同
        checkPassword = "123456789";
        result = userService.userRegister(userAccount,userPassword,checkPassword);
        Assertions.assertEquals(-1,result);

        //编写正确测试用例，插入数据
        checkPassword = "12345678";
        result = userService.userRegister(userAccount,userPassword,checkPassword);
        Assertions.assertTrue(result > 0);
    }
}