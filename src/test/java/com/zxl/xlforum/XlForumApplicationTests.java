package com.zxl.xlforum;

import com.zxl.xlforum.account.dto.req.AccountLoginRequest;
import com.zxl.xlforum.account.dto.req.AccountSignupRequest;
import com.zxl.xlforum.account.dto.resp.AccountBaseResponse;
import com.zxl.xlforum.account.service.AccountService;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
@Sql(scripts = {
        "classpath:testSql/Account.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class XlForumApplicationTests {

    @Autowired
    AccountService accountService;

    // ====================== 批量注册测试 ======================
    @ParameterizedTest(name = "[{index}] 注册测试: 用户名={0}, 密码={1}, 邮箱={2}")
    @CsvSource({
            "1234, 123456, 1670@qq.com",
            "张三, 123456, zhangsan@test.com",
            "李四, 123456, lisi@test.com"
    })
    @Order(1)
    @Commit
    public void batchSignupTest(String accountName, String password, String email) {
        AccountSignupRequest request = new AccountSignupRequest();
        request.setAccountName(accountName);
        request.setPassword(password);
        request.setEmail(email);

        AccountBaseResponse response = accountService.signup(request);

        assertNotNull(response);
        assertEquals("注册成功", response.getMessage());
    }

    // ====================== 登录测试 ======================
    @ParameterizedTest(name = "[{index}] 登录测试: 邮箱={0}, 密码={1}, 预期消息={2}")
    @MethodSource("loginTestData")
    @Order(2)
    @Commit
    public void loginTest(String email, String password, String expectedMessage) {
        AccountLoginRequest request = new AccountLoginRequest();
        request.setEmail(email);
        request.setPassword(password);

        AccountBaseResponse response = accountService.login(request);

        assertNotNull(response);
        assertEquals(expectedMessage, response.getMessage());
        System.out.println("Response: " + response);
    }

    private static Stream<Arguments> loginTestData() {
        return Stream.of(
                // 正常登录
                Arguments.of("1670@qq.com", "123456", "登录成功"),
                // 密码错误
                Arguments.of("1670@qq.com", "1234567", "登陆失败，密码错误"),
                // 账户不存在
                Arguments.of("660@qq.com", "1234567", "登录失败，账户不存在"),
                // 账户异常
                Arguments.of("lisi@test.com", "123456", "登录成功")
        );
    }

    // ====================== 注册测试 ======================
    @ParameterizedTest(name = "[{index}] 注册测试: 用户名={0}, 密码={1}, 邮箱={2}, 预期消息={3}")
    @MethodSource("signupTestData")
    @Order(3)
    @Commit
    public void signupTest(String accountName, String password, String email, String expectedMessage) {
        AccountSignupRequest request = new AccountSignupRequest();
        request.setAccountName(accountName);
        request.setPassword(password);
        request.setEmail(email);

        AccountBaseResponse response = accountService.signup(request);
        // 在测试中直接打印
        System.out.println("Response: " + response);
        assertNotNull(response);
        assertEquals(expectedMessage, response.getMessage());
    }

    private static Stream<Arguments> signupTestData() {
        return Stream.of(
                // 新用户注册
                Arguments.of("李四", "123456", "123@qq.com", "注册成功"),
                // 重复注册
                Arguments.of("王五", "123456", "1670@qq.com", "注册失败，账户已存在")
        );
    }

    // ====================== 修改密码测试 ======================
    @ParameterizedTest(name = "[{index}] 修改密码测试: 邮箱={0}, 旧密码={1}, 新密码={2}, 预期消息={3}")
    @MethodSource("updatePasswordTestData")
    @Order(4)
    @Commit
    public void updatePasswordTest(String email, String oldPassword, String newPassword, String expectedMessage) {
        AccountBaseResponse response = accountService.changePassword(email, oldPassword, newPassword);

        assertNotNull(response);
        assertEquals(expectedMessage, response.getMessage());
    }

    private static Stream<Arguments> updatePasswordTestData() {
        return Stream.of(
                // 正常修改密码
                Arguments.of("1670@qq.com", "123456", "1234567", "密码修改成功"),
                // 旧密码错误
                Arguments.of("1670@qq.com", "123456", "1234567", "密码修改失败，旧密码错误"),
                // 用户不存在
                Arguments.of("160@qq.com", "123456", "1234567", "修改密码失败，用户不存在"),
                // 修改另一个用户的密码
                Arguments.of("123@qq.com", "123456", "1234567", "密码修改成功")
        );
    }

    // ====================== 注销账户测试 ======================
    @ParameterizedTest(name = "[{index}] 注销测试: 邮箱={0}, 密码={1}, 预期消息={2}")
    @MethodSource("deleteAccountTestData")
    @Order(5)
    @Commit
    public void deleteAccountTest(String email, String password, String expectedMessage) {
        AccountBaseResponse response = accountService.signoff(email, password);

        assertNotNull(response);
        assertEquals(expectedMessage, response.getMessage());
    }

    private static Stream<Arguments> deleteAccountTestData() {
        return Stream.of(
                // 密码错误注销
                Arguments.of("123@qq.com", "123456", "密码错误，注销失败"),
                // 正常注销
                Arguments.of("123@qq.com", "1234567", "注销成功"),
                // 用户不存在
                Arguments.of("123@qq.com", "123456", "注销失败，用户不存在")
        );
    }
}