package com.zxl.xlforum.account.service;

import com.zxl.xlforum.account.dto.req.AccountLoginRequest;
import com.zxl.xlforum.account.dto.req.AccountSignupRequest;
import com.zxl.xlforum.account.dto.resp.AccountBaseResponse;
import com.zxl.xlforum.account.entity.Account;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountService {

    /**
     * 工具，使用email获取账号
     * @param email
     * @return 账号信息
     */
    private Account getAccountByEmail(String email) {
        return null;
    }

    /**
     * 工具，加密密码
     * @param password
     * @return 加密后的密码
     */
    private String encodePassword(String password) {
        return null;
    }

    /**
     * 注册服务
     * @param accountSignupRequest
     * @return
     */
    public AccountBaseResponse signup(AccountSignupRequest accountSignupRequest) {
        //todo 用email查重
        //todo 加密密码
        //todo 执行新增
        return null;
    }

    /**
     * 登录服务，需要传入用户名和密码
     * @param accountLoginRequest
     * @return
     */
    public AccountBaseResponse login(AccountLoginRequest accountLoginRequest) {
        //todo 用email查询
        //todo 对比密码
        //todo 生产token与消息
        return null;
    }

    /**
     * 修改密码
     * @param email
     * @param oldPassword
     * @param newPassword
     * @return
     */
    public AccountBaseResponse changePassword(String email, String oldPassword,String newPassword) {
        //todo 用email查询
        //todo 比较旧密码
        //todo 加密密码
        //todo 执行更新
        //todo 生产新的token与消息
        return null;
    }

    /**
     * 注销账号
     * @param email
     * @param password
     * @return
     */
    public AccountBaseResponse signoff(String email, String password) {
        //todo 用email查询
        //todo 比较密码
        //todo 执行删除并生成消息
        return null;
    }
}
