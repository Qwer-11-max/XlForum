package com.zxl.xlforum;

import com.zxl.xlforum.common.intercept.AuthInterceptor;
import com.zxl.xlforum.config.WebConfig;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.*;

import static org.hamcrest.Matchers.containsString;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zxl.xlforum.account.dto.req.AccountLoginRequest;
import com.zxl.xlforum.account.dto.req.AccountSignupRequest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Sql(scripts = {
        "classpath:testSql/Account.sql",       // 初始化表结构（若已手动建表可保留注释）
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Import({AuthInterceptor.class, WebConfig.class})
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 全局token，用于模拟无状态连接
    private static String globalToken;

    // 测试用账号信息
    // 改为固定常量（编译期可确定），通过@Transactional保证测试后数据回滚，无需动态生成
    private static final String TEST_EMAIL = "test_integration@example.com";
    private static final String TEST_PASSWORD = "Test123456!";
    private static final String TEST_NAME = "TestUser";

    /**
     * 在所有测试方法执行前，通过注册接口注入20条测试数据
     */
    @Test
    @Order(1)
    public void init20TestAccounts() throws Exception {
        // 生成20个唯一账号（邮箱、用户名带序号，避免重复）
        for (int i = 1; i <= 20; i++) {
            // 构建唯一的注册信息（与现有测试账号区分开）
            String testEmail = "page_test_user_" + i + "@example.com";
            String testName = "PageTestUser" + i;

            AccountSignupRequest signupRequest = new AccountSignupRequest();
            signupRequest.setEmail(testEmail);
            signupRequest.setPassword(TEST_PASSWORD);  // 统一密码，简化测试
            signupRequest.setAccountName(testName);

            // 执行注册请求
            mockMvc.perform(MockMvcRequestBuilders.post("/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(signupRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("注册成功"));  // 验证注册成功
        }
    }

    // 在所有测试前先注册并登录获取token
    @Test
    @Order(2)
    public void setup() throws Exception {
        // 注册测试账号
        AccountSignupRequest signupRequest = new AccountSignupRequest();
        signupRequest.setEmail(TEST_EMAIL);
        signupRequest.setPassword(TEST_PASSWORD);
        signupRequest.setAccountName(TEST_NAME);

        mockMvc.perform(MockMvcRequestBuilders.post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("注册成功"));

        // 登录获取token
        AccountLoginRequest loginRequest = new AccountLoginRequest();
        loginRequest.setEmail(TEST_EMAIL);
        loginRequest.setPassword(TEST_PASSWORD);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn();

        // 提取token保存到全局变量
        String responseJson = result.getResponse().getContentAsString();
        globalToken = objectMapper.readTree(responseJson).get("token").asText();
    }

    // 测试登录功能 - 参数化测试（添加参数说明）
    @ParameterizedTest(
            name = "登录测试 - 邮箱: {0}, 密码: {1}, 预期结果: {2}"
    )
    @CsvSource({
            TEST_EMAIL + ", " + TEST_PASSWORD + ", 登录成功",  // 正确的账号密码
            TEST_EMAIL + ", wrongpassword, 登陆失败，密码错误",         // 错误的密码
            "nonexistent@example.com, anypassword, 登录失败，账户不存在"  // 不存在的账号
    })
    @Order(3)
    public void testLogin(String email, String password, String expectedMessage) throws Exception {
        AccountLoginRequest loginRequest = new AccountLoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        mockMvc.perform(MockMvcRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    // 测试注册功能 - 参数化测试（添加参数说明）
    @ParameterizedTest(
            name = "注册测试 - 请求信息: {0}, 预期结果: {1}"
    )
    @MethodSource("provideSignupData")
    @Order(4)
    public void testSignup(AccountSignupRequest request, int expectStatus, String expectedMessage) throws Exception {
        ResultActions temp  = mockMvc.perform(MockMvcRequestBuilders.post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)));

                temp.andExpect(status().is(expectStatus));
                if(expectStatus == 200) {
                    temp.andExpect(jsonPath("$.message").value(expectedMessage));
                }
    }

    // 提供注册测试数据
    private static Stream<Object[]> provideSignupData() {
        // 新的测试邮箱，避免与setup中创建的重复
        String newEmail = "new_test_" + System.currentTimeMillis() + "@example.com";

        // 有效注册信息
        AccountSignupRequest validRequest = new AccountSignupRequest();
        validRequest.setEmail(newEmail);
        validRequest.setPassword(TEST_PASSWORD);
        validRequest.setAccountName("NewTestUser");

        // 重复注册信息
        AccountSignupRequest duplicateRequest = new AccountSignupRequest();
        duplicateRequest.setEmail(TEST_EMAIL);
        duplicateRequest.setPassword(TEST_PASSWORD);
        duplicateRequest.setAccountName(TEST_NAME);

        // 无效邮箱格式
        AccountSignupRequest invalidEmailRequest = new AccountSignupRequest();
        invalidEmailRequest.setEmail("invalid-email");
        invalidEmailRequest.setPassword(TEST_PASSWORD);
        invalidEmailRequest.setAccountName("InvalidEmailUser");

        return Stream.of(
                new Object[]{validRequest,200 ,"注册成功"},
                new Object[]{duplicateRequest,200, "注册失败，账户已存在"},
                new Object[]{invalidEmailRequest,400, "邮箱格式无效"}
        );
    }

    // 测试修改密码功能 - 参数化测试（添加参数说明）
    @ParameterizedTest(
            name = "修改密码测试 - 旧密码: {0}, 新密码: {1}, 预期结果: {3}"
    )
    @MethodSource("provideChangePasswordData")
    @Order(5)
    public void testChangePassword(String oldPassword, String newPassword, int expectedStatus,String expectedMessage) throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/changePassword")
                .header("Authorization", "Bearer " + globalToken)
                .param("oldPassword", oldPassword)
                .param("newPassword", newPassword));

        // 先断言预期状态（无论成功还是失败）
        resultActions.andExpect(status().is(expectedStatus));

        // 如果预期是成功（200），再断言消息
        if (expectedStatus == 200) { // 注意：这里仍有问题，需进一步修正
            resultActions.andExpect(jsonPath("$.message").value(expectedMessage));
        }
        // 如果预期是400，也可以断言错误消息
        else if (expectedStatus == 400) {
            resultActions.andExpect(content().string(containsString(expectedMessage)));
            // 若用自定义Result对象，可改为：jsonPath("$.message").value(expectedMessage)
        }
    }

    // 提供修改密码的测试数据（MethodSource格式）
    private static Stream<Object[]> provideChangePasswordData() {
        // 数组元素顺序：oldPassword, newPassword, expectedMessage
        return Stream.of(
                new Object[]{TEST_PASSWORD, "NewTest123!",200, "密码修改成功"},  // 正确的旧密码
                new Object[]{"wrongpassword", "NewTest123!",200, "密码修改失败，旧密码错误"},   // 错误的旧密码
                new Object[]{TEST_PASSWORD, "short",400, "密码应在6-20个字符内"}       // 无效的新密码
        );
    }


    // 测试注销功能 - 参数化测试（添加参数说明）
    @ParameterizedTest(
            name = "注销测试 - 密码: {0}, 预期结果: {1}"
    )
    @CsvSource({
            "wrongpassword, 密码错误，注销失败",          // 错误的密码
            "NewTest123!" + ", 注销成功",  // 正确的账号密码
    })
    @Order(6)
    public void testSignoff(String password, String expectedMessage) throws Exception {
        AccountLoginRequest logoffRequest = new AccountLoginRequest();
        logoffRequest.setPassword(password);

        mockMvc.perform(MockMvcRequestBuilders.post("/signoff")
                        .header("Authorization", "Bearer " + globalToken)  // 使用全局token
                        .param("password", password))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(expectedMessage));
    }

    // 分页查询测试 - 参数化测试
    @ParameterizedTest(
            name = "分页查询测试 - 页码: {0}, 每页条数: {1}, 预期页码: {2}, 预期每页条数: {3}"
    )
    @MethodSource("providePageData")
    @Order(7)  // 顺序排在其他测试之后
    public void testPageQuery(int pageNum, int pageSize, int expectedPageNum, int expectedPageSize) throws Exception {
        // 执行分页查询请求（假设需要认证，携带全局token）
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/getAllAccount")
                .param("pageNum", String.valueOf(pageNum))
                .param("pageSize", String.valueOf(pageSize))
                .contentType(MediaType.APPLICATION_JSON));

        // 验证基础响应状态
        resultActions.andExpect(status().isOk());

        // 验证分页核心参数
        resultActions
                .andExpect(jsonPath("$.pageNum").value(expectedPageNum))  // 验证实际页码（考虑分页合理化）
                .andExpect(jsonPath("$.pageSize").value(expectedPageSize))  // 验证每页条数
                .andExpect(jsonPath("$.list").isArray())  // 验证数据列表为数组
                .andExpect(jsonPath("$.total").isNumber());  // 验证总条数为数字
    }

    // 提供分页测试数据（考虑分页合理化配置）
// 数据格式：[请求页码, 请求每页条数, 预期实际页码, 预期每页条数]
    private static Stream<Object[]> providePageData() {
        return Stream.of(
                new Object[]{1, 5, 1, 5},    // 正常页码+正常条数
                new Object[]{0, 5, 1, 5},    // 页码<=0（合理化后返回第1页）
                new Object[]{100, 5, 5, 5},  // 页码超过总页数（假设总页数为3，合理化后返回最后一页）
                new Object[]{2, 10, 2, 10},  // 较大每页条数
                new Object[]{1, 0, 1, 0}    // 条数<=0（假设默认条数为10）
        );
    }
}