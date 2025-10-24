package com.zxl.xlforum;

import com.zxl.xlforum.account.dto.req.AccountLoginRequest;
import com.zxl.xlforum.account.dto.req.AccountSignupRequest;
import com.zxl.xlforum.account.dto.resp.AccountBaseResponse;
import com.zxl.xlforum.account.entity.Account;
import com.zxl.xlforum.account.service.AccountService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@Sql(scripts = {"classpath:testSql/Account.sql","classpath:testSql/insertAccount.sql"},
//        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class XlForumApplicationTests {

    @Autowired
    AccountService  accountService;

    @Test
    @Order(1)
    public void loginTest(){
        //==============1============
        AccountLoginRequest loginRequest = new AccountLoginRequest();
        loginRequest.setEmail("1670@qq.com");
        loginRequest.setPassword("123456");

        AccountBaseResponse resp = accountService.login(loginRequest);

        assert resp != null;
        assert resp.getMessage().equals("登录成功");

        //==========2==========
        loginRequest.setEmail("1670@qq.com");
        loginRequest.setPassword("1234567");

        resp = accountService.login(loginRequest);

        assert resp != null;
        assert resp.getMessage().equals("登陆失败，密码错误");

        //=============3========
        loginRequest.setEmail("660@qq.com");
        loginRequest.setPassword("1234567");

        resp = accountService.login(loginRequest);

        assert resp != null;
        assert resp.getMessage().equals("登录失败，账户不存在");

        //=============4========
        loginRequest.setEmail("lisi@test.com");
        loginRequest.setPassword("123456");

        resp = accountService.login(loginRequest);

        assert resp != null;
        assert resp.getMessage().equals("账户异常");
    }

    @Test
    @Order(2)
    public void insertAccountTest(){
        // 准备数据
        AccountSignupRequest account = new AccountSignupRequest();
        account.setAccountName("李四")
                .setPassword("123456")
                .setEmail("123@qq.com");
        AccountBaseResponse resp = accountService.signup(account);

        assert resp != null;
        assert resp.getMessage().equals("注册成功");

        account.setAccountName("王五")
                .setPassword("123456")
                .setEmail("1670@qq.com");
        resp = accountService.signup(account);
        assert resp != null;
        assert resp.getMessage().equals("注册失败，账户已存在");
    }

    @Test
    @Order(3)
    public void updateAccountTest(){
        //=============1=================
        String email = "1670@qq.com";
        String oldPassword = "123456";
        String newPassword = "1234567";

        AccountBaseResponse resp = accountService.changePassword(email, oldPassword, newPassword);

        assert resp != null;
        assert resp.getMessage().equals("密码修改成功");

        //==============2=================
        email = "1670@qq.com";
        oldPassword = "123456";
        newPassword = "1234567";

        resp = accountService.changePassword(email, oldPassword, newPassword);

        assert resp != null;
        assert resp.getMessage().equals("密码修改失败，旧密码错误");

        //==============3=================
        email = "160@qq.com";
        oldPassword = "123456";
        newPassword = "1234567";

        resp = accountService.changePassword(email, oldPassword, newPassword);

        assert resp != null;
        assert resp.getMessage().equals("修改密码失败，用户不存在");

        //=========4===============
        email = "123@qq.com";
        oldPassword = "123456";
        newPassword = "1234567";

        resp = accountService.changePassword(email, oldPassword, newPassword);

        assert resp != null;
        assert resp.getMessage().equals("密码修改成功");
    }

    @Test
    @Order(4)
    public void deleteAccountTest(){
        //======1==========
        String email = "123@qq.com";
        String pwd = "123456";

        AccountBaseResponse resp = accountService.signoff(email, pwd);

        assert resp != null;
        assert resp.getMessage().equals("密码错误，注销失败");

        //=========2===========
        email = "123@qq.com";
        pwd = "1234567";

        resp = accountService.signoff(email, pwd);

        assert resp != null;
        assert resp.getMessage().equals("注销成功");

        //=========3===========
        email = "123@qq.com";
        pwd = "123456";

        resp = accountService.signoff(email, pwd);

        assert resp != null;
        assert resp.getMessage().equals("注销失败，用户不存在");
    }
}
