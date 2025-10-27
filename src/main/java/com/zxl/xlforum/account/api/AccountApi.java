package com.zxl.xlforum.account.api;

import com.zxl.xlforum.account.dto.req.AccountLoginRequest;
import com.zxl.xlforum.account.dto.req.AccountSignupRequest;
import com.zxl.xlforum.account.dto.resp.AccountBaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
public interface AccountApi {
    /**
     * 登录，需要传入邮箱和密码
     * @param accountLoginRequest
     * @return JWTtoken,用户信息,消息机制
     */
    ResponseEntity<?> login(
            @Valid @RequestBody
            AccountLoginRequest accountLoginRequest
    );

    /**
     * 注册，需要传入邮箱，密码和用户名
     * @param accountSignupRequest
     * @return 注册消息
     */
    ResponseEntity<?> signup(
            @Valid @RequestBody
            AccountSignupRequest accountSignupRequest
    );

    /**
     * 修改密码，在登陆后传入新密码和旧密码
     * @param email
     * @param oldPassword
     * @param newPassword
     * @return JWT新的token以及用户基本信息
     */
    ResponseEntity<?> changePassword(
            @Email
                String email,
            @Parameter(description = "用户密码")
            @Size(min = 6, max = 20, message = "密码应在6-20个字符内")
            @RequestParam
                String oldPassword,
            @Parameter(description = "用户密码")
            @Size(min = 6, max = 20, message = "密码应在6-20个字符内")
            @RequestParam
                String newPassword);

    /**
     * 注销用户，需要传入邮箱及密码
     * @param accountLogoffRequest
     * @return
     */
    ResponseEntity<?> signoff(
            @Valid @RequestBody
            AccountLoginRequest accountLogoffRequest
    );
}
