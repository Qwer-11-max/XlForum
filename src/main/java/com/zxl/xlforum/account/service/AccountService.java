package com.zxl.xlforum.account.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zxl.xlforum.account.dto.req.AccountLoginRequest;
import com.zxl.xlforum.account.dto.req.AccountSignupRequest;
import com.zxl.xlforum.account.dto.resp.AccountBaseResponse;
import com.zxl.xlforum.account.entity.Account;
import com.zxl.xlforum.account.mapper.AccountMapper;
import com.zxl.xlforum.common.security.Argon2PasswordEncoder;
import com.zxl.xlforum.common.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AccountService {

    @Autowired
    AccountMapper accountMapper;

    @Autowired
    Argon2PasswordEncoder  argon2PasswordEncoder;
    @Autowired
    private JwtUtils jwtUtils;

    /**
     * 工具，使用email获取账号
     * @param email
     * @return 账号信息
     */
    @Cacheable(value = "accountCache",key = "'account::email::' + #email",unless = "'#result == null'")
    public Account getAccountByEmail(String email) {
        return accountMapper.selectAccountByEmail(email);
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

        // 加密密码
        String pwd = argon2PasswordEncoder.encode(accountSignupRequest.getPassword());

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
        // 用email查询
        Account account = getAccountByEmail(accountLoginRequest.getEmail());
        if (account == null) {
            resp.setMessage("登录失败，账户不存在");
            return resp;
        }
        if (account.getAccountStatus() != 0) {
            resp.setMessage("账户异常");
            return resp;
        }


        if (argon2PasswordEncoder.matches(accountLoginRequest.getPassword(), account.getPassword())) {
            // 使用email生成token
            String token = jwtUtils.generateJwtToken(account.getEmail());
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
    @CachePut(value = "accountCache",key = "'account::email::' + #email",unless = "#result == null")
    public AccountBaseResponse changePassword(String email, String oldPassword,String newPassword) {
        // 用email查询
        AccountBaseResponse resp = new AccountBaseResponse();
        Account account = getAccountByEmail(email);
        if (account == null) {
            resp.setMessage("修改密码失败，用户不存在");
            return resp;
        }

        if(argon2PasswordEncoder.matches(oldPassword, account.getPassword())) {
            // 加密密码
            String newPwd = argon2PasswordEncoder.encode(newPassword);

            // 执行更新
            Account updateAccount = new Account();

            updateAccount.setAccountId(account.getAccountId());
            updateAccount.setEmail(email);
            updateAccount.setPassword(newPwd);

            accountMapper.updateAccount(updateAccount);
            // 生成新的token
            String token = jwtUtils.generateJwtToken(account.getEmail());

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
    @CacheEvict(value = "accountCache",key = "'account::email::' + #email")
    public AccountBaseResponse signoff(String email, String password) {
        // 用email查询
        AccountBaseResponse resp = new AccountBaseResponse();
        Account account = getAccountByEmail(email);
        if (account == null) {
            resp.setMessage("注销失败，用户不存在");
            return  resp;
        }

        if(argon2PasswordEncoder.matches(password, account.getPassword())) {
            accountMapper.deleteAccountByAccountId(account.getAccountId());

            resp.setMessage("注销成功");
        }else{
            resp.setMessage("密码错误，注销失败");
        }

        return resp;
    }

    /**
     * 分页查询用户
     * @param pageNum 页码（从1开始）
     * @param pageSize 每页条数
     * @return 分页结果（包含数据和分页信息）
     */
    public PageInfo<String> getAccountByPage(int pageNum, int pageSize) {
        // 开启分页：必须在查询方法前调用
        Page<Object> objects = PageHelper.startPage(pageNum, pageSize);
        // 执行查询：返回的List实际是Page对象（包含分页信息）
        List<String> accountList = accountMapper.findAll();
        // 强转为Page对象（或直接返回，Page是List的子类）
        return new PageInfo<>(accountList);
    }
}
