package com.zxl.xlforum;

import com.zxl.xlforum.account.dto.req.AccountLoginRequest;
import com.zxl.xlforum.account.dto.req.AccountSignupRequest;
import com.zxl.xlforum.account.dto.resp.AccountBaseResponse;
import com.zxl.xlforum.account.service.AccountService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

// 导入 JUnit 5 断言工具类（静态导入，直接使用方法名）
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
// 建议恢复 SQL 初始化（否则 loginTest 依赖的用户不存在，会导致测试失败）
@Sql(scripts = {
        "classpath:testSql/Account.sql",       // 初始化表结构（若已手动建表可保留注释）
        "classpath:testSql/insertAccount.sql"  // 插入 loginTest 依赖的测试用户（如1670@qq.com、lisi@test.com）
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
// 要避免数据库删除操作，这次只是试用，不要做复杂的测试
class XlForumApplicationTests {

    @Autowired
    AccountService accountService;

    @Test
    @Order(1)
    @Commit // 确保 loginTest 执行后事务提交（若后续方法依赖此方法数据，可保留；否则可删除）
    public void loginTest() {
        // ==============1：正常登录（依赖 insertAccount.sql 中的 1670@qq.com 用户）============
        AccountLoginRequest loginRequest = new AccountLoginRequest();
        loginRequest.setEmail("1670@qq.com");
        loginRequest.setPassword("123456");

        AccountBaseResponse resp = accountService.login(loginRequest);
        assertNotNull(resp); // 断言响应不为空
        assertEquals("登录成功", resp.getMessage()); // 断言响应消息匹配

        // ==========2：密码错误============
        loginRequest.setEmail("1670@qq.com");
        loginRequest.setPassword("1234567");
        resp = accountService.login(loginRequest);

        assertNotNull(resp);
        assertEquals("登陆失败，密码错误", resp.getMessage());

        // ============3：账户不存在============
        loginRequest.setEmail("660@qq.com");
        loginRequest.setPassword("1234567");
        resp = accountService.login(loginRequest);

        assertNotNull(resp);
        assertEquals("登录失败，账户不存在", resp.getMessage());

        // ============4：账户异常（依赖 insertAccount.sql 中的 lisi@test.com 用户）============
        loginRequest.setEmail("lisi@test.com");
        loginRequest.setPassword("123456");
        resp = accountService.login(loginRequest);

        assertNotNull(resp);
        assertEquals("账户异常", resp.getMessage());
    }

    @Test
    @Order(2)
    @Commit
    public void insertAccountTest() {
        // 准备数据：注册新用户（123@qq.com）
        AccountSignupRequest account = new AccountSignupRequest();
        account.setAccountName("李四")
                .setPassword("123456")
                .setEmail("123@qq.com");
        AccountBaseResponse resp = accountService.signup(account);

        assertNotNull(resp);
        assertEquals("注册成功", resp.getMessage());

        // 重复注册：1670@qq.com（已在 insertAccount.sql 中存在）
        account.setAccountName("王五")
                .setPassword("123456")
                .setEmail("1670@qq.com");
        resp = accountService.signup(account);

        assertNotNull(resp);
        assertEquals("注册失败，账户已存在", resp.getMessage());
    }

    @Test
    @Order(3)
    @Commit
    public void updateAccountTest() {
        // =============1：正常修改密码（1670@qq.com，原密码123456）============
        String email = "1670@qq.com";
        String oldPassword = "123456";
        String newPassword = "1234567";
        AccountBaseResponse resp = accountService.changePassword(email, oldPassword, newPassword);

        assertNotNull(resp);
        assertEquals("密码修改成功", resp.getMessage());

        // =============2：旧密码错误（1670@qq.com，旧密码仍用123456）============
        email = "1670@qq.com";
        oldPassword = "123456";
        newPassword = "1234567";
        resp = accountService.changePassword(email, oldPassword, newPassword);

        assertNotNull(resp);
        assertEquals("密码修改失败，旧密码错误", resp.getMessage());

        // =============3：用户不存在（160@qq.com）============
        email = "160@qq.com";
        oldPassword = "123456";
        newPassword = "1234567";
        resp = accountService.changePassword(email, oldPassword, newPassword);

        assertNotNull(resp);
        assertEquals("修改密码失败，用户不存在", resp.getMessage());

        // ==========4：正常修改密码（123@qq.com，注册于 insertAccountTest）============
        email = "123@qq.com";
        oldPassword = "123456";
        newPassword = "1234567";
        resp = accountService.changePassword(email, oldPassword, newPassword);

        assertNotNull(resp);
        assertEquals("密码修改成功", resp.getMessage());
    }

    @Test
    @Order(4)
    @Commit
    public void deleteAccountTest() {
        // =======1：密码错误（123@qq.com，原密码123456，已在 updateAccountTest 中改为1234567）======
        String email = "123@qq.com";
        String pwd = "123456";
        AccountBaseResponse resp = accountService.signoff(email, pwd);

        assertNotNull(resp);
        assertEquals("密码错误，注销失败", resp.getMessage());

        // ==========2：正常注销（123@qq.com，使用修改后的密码1234567）===========
        email = "123@qq.com";
        pwd = "1234567";
        resp = accountService.signoff(email, pwd);

        assertNotNull(resp);
        assertEquals("注销成功", resp.getMessage());

        // ==========3：用户不存在（123@qq.com 已注销）===========
        email = "123@qq.com";
        pwd = "123456";
        resp = accountService.signoff(email, pwd);

        assertNotNull(resp);
        assertEquals("注销失败，用户不存在", resp.getMessage());
    }
}