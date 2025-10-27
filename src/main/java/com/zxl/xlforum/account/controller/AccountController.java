package com.zxl.xlforum.account.controller;

import com.zxl.xlforum.account.api.AccountApi;
import com.zxl.xlforum.account.dto.req.AccountLoginRequest;
import com.zxl.xlforum.account.dto.req.AccountSignupRequest;
import com.zxl.xlforum.account.dto.resp.AccountBaseResponse;
import com.zxl.xlforum.account.dto.resp.JwtResponse;
import com.zxl.xlforum.account.security.JwtUtils;
import com.zxl.xlforum.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Account",description = "账户操作")
@RestController
@RequestMapping("/")
public class AccountController implements AccountApi {
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AccountService accountService;

    @Operation(description = "登录")
    @PostMapping("/login")
    @Override
    public ResponseEntity<?> login(AccountLoginRequest accountLoginRequest) {
        AccountBaseResponse resp = accountService.login(accountLoginRequest);
        return ResponseEntity.ok(resp);
    }

    @Operation(description = "注册")
    @PostMapping("/signup")
    @Override
    public ResponseEntity<?> signup(AccountSignupRequest accountSignupRequest) {
        //todo 调用服务进行注册
        AccountBaseResponse resp =  accountService.signup(accountSignupRequest);
        //todo 封装消息和状态
        return ResponseEntity.ok(resp);
    }

    @Operation(description = "修改密码")
    @PostMapping("/changePassword")
    @Override
    public ResponseEntity<?> changePassword(String email,
                                            String oldPassword,
                                            String newPassword) {
        //todo 将email和新旧密码传入服务
        AccountBaseResponse resp = accountService.changePassword(email,oldPassword,newPassword);
        //todo 返回状态
        return ResponseEntity.ok(resp);
    }

    @Operation(description = "注销用户")
    @PostMapping("/signoff")
    @Override
    public ResponseEntity<?> signoff(AccountLoginRequest accountLogoffRequest) {
        AccountBaseResponse resp = accountService.signoff(accountLogoffRequest.getEmail(),accountLogoffRequest.getPassword());
        return ResponseEntity.ok(resp);
    }
}
