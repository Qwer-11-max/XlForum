package com.zxl.xlforum.account.service;

import com.zxl.xlforum.account.dto.req.AccountLoginRequest;
import com.zxl.xlforum.account.dto.req.AccountSignupRequest;
import com.zxl.xlforum.account.dto.resp.AccountBaseResponse;
import com.zxl.xlforum.account.entity.Account;
import com.zxl.xlforum.account.mapper.AccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountService {

    @Autowired
    AccountMapper accountMapper;

    /**
     * 工具，使用email获取账号
     * @param email
     * @return 账号信息
     */
    private Account getAccountByEmail(String email) {
        return accountMapper.selectAccountByEmail(email);
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
        // 用email查重
        AccountBaseResponse resp = new AccountBaseResponse();
        Account account = getAccountByEmail(accountSignupRequest.getEmail());
        if (account != null) {
            resp.setMessage("注册失败，账户已存在");
            return  resp;
        }

        //todo 加密密码
        String pwd = accountSignupRequest.getPassword();

        // 执行新增
        Account insertAccount = new Account();
        insertAccount.setEmail(accountSignupRequest.getEmail());
        insertAccount.setPassword(pwd);
        insertAccount.setAccountName(accountSignupRequest.getAccountName());
        insertAccount.setAccountStatus(0);

        accountMapper.insertAccount(insertAccount);

        resp.setMessage("注册成功");
        return resp;
    }

    /**
     * 登录服务，需要传入用户名和密码
     * @param accountLoginRequest
     * @return
     */
    public AccountBaseResponse login(AccountLoginRequest accountLoginRequest) {
        AccountBaseResponse resp = new AccountBaseResponse();
        //todo 用email查询
        Account account = getAccountByEmail(accountLoginRequest.getEmail());
        if (account == null) {
            resp.setMessage("登录失败，账户不存在");
            return resp;
        }
        if (account.getAccountStatus() != 0) {
            resp.setMessage("账户异常");
            return resp;
        }

        //todo 对比密码
        String pwd = accountLoginRequest.getPassword();

        if (account.getPassword().equals(pwd)) {
            //todo 使用email生成token
            String token = "123";
            resp.setToken(token);

            resp.setAccountName(account.getAccountName());
            resp.setAccountStatus(0);
            resp.setAccountId(account.getAccountId());
            resp.setEmail(account.getEmail());

            resp.setMessage("登录成功");
        } else {
            resp.setMessage("登陆失败，密码错误");
        }
        return resp;
    }

    /**
     * 修改密码
     * @param email
     * @param oldPassword
     * @param newPassword
     * @return
     */
    public AccountBaseResponse changePassword(String email, String oldPassword,String newPassword) {
        // 用email查询
        AccountBaseResponse resp = new AccountBaseResponse();
        Account account = getAccountByEmail(email);
        if (account == null) {
            resp.setMessage("修改密码失败，用户不存在");
            return resp;
        }

        // 比较旧密码
        String pwd = oldPassword;

        if(account.getPassword().equals(pwd)) {
            // 加密密码
            String newPwd = newPassword;

            // 执行更新
            Account updateAccount = new Account();

            updateAccount.setAccountId(account.getAccountId());
            updateAccount.setEmail(email);
            updateAccount.setPassword(newPwd);

            accountMapper.updateAccount(updateAccount);
            // 生成新的token
            String token = "123";

            resp.setToken(token);
            resp.setAccountName(account.getAccountName());
            resp.setAccountStatus(0);
            resp.setAccountId(account.getAccountId());
            resp.setEmail(account.getEmail());

            resp.setMessage("密码修改成功");
            return resp;
        }else{
            resp.setMessage("密码修改失败，旧密码错误");
            return resp;
        }
    }

    /**
     * 注销账号
     * @param email
     * @param password
     * @return
     */
    public AccountBaseResponse signoff(String email, String password) {
        // 用email查询
        AccountBaseResponse resp = new AccountBaseResponse();
        Account account = getAccountByEmail(email);
        if (account == null) {
            resp.setMessage("注销失败，用户不存在");
            return  resp;
        }
        // 比较密码
        String pwd = password;
        if(account.getPassword().equals(pwd)) {
            accountMapper.deleteAccountByAccountId(account.getAccountId());

            resp.setMessage("注销成功");
        }else{
            resp.setMessage("密码错误，注销失败");
        }

        return resp;
    }
}
