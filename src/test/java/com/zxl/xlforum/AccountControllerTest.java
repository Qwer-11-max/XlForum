package com.zxl.xlforum;

import com.zxl.xlforum.account.controller.AccountController;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = AccountController.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Sql(scripts = {
        "classpath:testSql/Account.sql",       // 初始化表结构（若已手动建表可保留注释）
        "classpath:testSql/insertAccount.sql"  // 插入 loginTest 依赖的测试用户（如1670@qq.com、lisi@test.com）
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

}
