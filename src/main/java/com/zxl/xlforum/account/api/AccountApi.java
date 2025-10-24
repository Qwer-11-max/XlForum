package com.zxl.xlforum.account.api;

import com.zxl.xlforum.account.dto.req.AccountBaseRequest;
import com.zxl.xlforum.account.dto.req.AccountLoginRequest;
import com.zxl.xlforum.account.dto.req.AccountSignupRequest;
import com.zxl.xlforum.account.dto.resp.AccountBaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
@Validated
public interface AccountApi {

    /**
     * 登录，需要传入邮箱和密码
     * @param accountLoginRequest
     * @return JWTtoken以及用户信息
     */
    @Operation(description = "登录")
    @PostMapping("/login")
    ResponseEntity<AccountBaseResponse> login(@Valid @RequestBody AccountLoginRequest accountLoginRequest);

    /**
     * 注册，需要传入邮箱，密码和用户名
     * @param accountSignupRequest
     * @return 注册状态
     */
    @Operation(description = "注册")
    @PostMapping("signup")
    ResponseEntity<AccountBaseResponse> signup(@Valid @RequestBody AccountSignupRequest accountSignupRequest);

    /**
     * 修改密码，在登陆后传入新密码和旧密码
     * @param oldPassword
     * @param newPassword
     * @return JWT新的token以及用户基本信息
     */
    @Operation(description = "修改密码")
    @PostMapping("changePassword")
    ResponseEntity<AccountBaseResponse> changePassword(@Parameter(description = "用户密码")
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
    @Operation(description = "注销用户")
    @PostMapping("signoff")
    ResponseEntity<AccountBaseResponse> signoff(@Valid @RequestBody AccountLoginRequest accountLogoffRequest);
}
