package com.zxl.xlforum.account.controller;

import com.zxl.xlforum.account.api.AccountApi;
import com.zxl.xlforum.account.dto.req.AccountLoginRequest;
import com.zxl.xlforum.account.dto.req.AccountSignupRequest;
import com.zxl.xlforum.account.dto.resp.AccountBaseResponse;
import org.springframework.http.ResponseEntity;

public class AccountController implements AccountApi {

    @Override
    public ResponseEntity<AccountBaseResponse> login(AccountLoginRequest accountLoginRequest) {
        //todo 验证用户名和密码
        //todo 返回登录信息以及状态
        return null;
    }

    @Override
    public ResponseEntity<AccountBaseResponse> signup(AccountSignupRequest accountSignupRequest) {
        //todo 调用服务进行注册
        //todo 封装消息和状态
        return null;
    }

    @Override
    public ResponseEntity<AccountBaseResponse> changePassword(String oldPassword, String newPassword) {
        //todo 验证登录状态
        //todo 提取email
        //todo 将email和新旧密码传入服务
        //todo 返回状态
        return null;
    }

    @Override
    public ResponseEntity<AccountBaseResponse> signoff(AccountLoginRequest accountLogoffRequest) {
        //todo 验证用户登录状态
        //todo 提取email
        //todo 将email与密码传入服务
        //todo 返回删除信息
        return null;
    }
}
