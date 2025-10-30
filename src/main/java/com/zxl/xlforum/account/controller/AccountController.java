package com.zxl.xlforum.account.controller;

import com.github.pagehelper.PageInfo;
import com.zxl.xlforum.account.api.AccountApi;
import com.zxl.xlforum.account.dto.req.AccountLoginRequest;
import com.zxl.xlforum.account.dto.req.AccountSignupRequest;
import com.zxl.xlforum.account.dto.resp.AccountBaseResponse;
import com.zxl.xlforum.common.security.JwtUtils;
import com.zxl.xlforum.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Account",description = "账户操作")
@RestController
@Validated
public class AccountController implements AccountApi {
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AccountService accountService;

    @Operation(description = "登录")
    @PostMapping("/login")
    @Override
    public ResponseEntity<?> login(@RequestBody AccountLoginRequest accountLoginRequest) {
        AccountBaseResponse resp = accountService.login(accountLoginRequest);
        return ResponseEntity.ok(resp);
    }

    @Operation(description = "注册")
    @PostMapping("/signup")
    @Override
    public ResponseEntity<?> signup(@RequestBody AccountSignupRequest accountSignupRequest) {
        // 调用服务进行注册
        AccountBaseResponse resp =  accountService.signup(accountSignupRequest);
        // 封装消息和状态
        return ResponseEntity.ok(resp);
    }

    @Operation(description = "修改密码")
    @PostMapping("/changePassword")
    @Override
    public ResponseEntity<?> changePassword(@RequestAttribute String email,
                                            @RequestParam String oldPassword,
                                            @RequestParam String newPassword) {
        // 将email和新旧密码传入服务
        AccountBaseResponse resp = accountService.changePassword(email,oldPassword,newPassword);
        // 返回状态
        return ResponseEntity.ok(resp);
    }

    @Operation(description = "注销用户")
    @PostMapping("/signoff")
    @Override
    public ResponseEntity<?> signoff(@RequestAttribute String email,@RequestParam String password) {
        AccountBaseResponse resp = accountService.signoff(email,password);
        return ResponseEntity.ok(resp);
    }

    @Override
    public ResponseEntity<PageInfo<?>> getAllAccount(int pageNum,int pageSize) {
        PageInfo<String> resp = accountService.getAccountByPage(pageNum,pageSize);
        return ResponseEntity.ok(resp);
    }
}
