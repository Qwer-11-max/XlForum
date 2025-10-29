package com.zxl.xlforum.account.mapper;

import com.zxl.xlforum.account.entity.Account;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AccountMapper {
    List<String> findAll();
    Account selectAccountByEmail(String email);
    int deleteAccountByAccountId(Integer accountId);
    int insertAccount(Account account);
    int updateAccount(Account account);
}
