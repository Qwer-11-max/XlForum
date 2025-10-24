package com.zxl.xlforum.account.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer accountId; //账户ID
    private String accountName; //账户名称
    private String password; //账户密码
    private String email; //账户email
    private Integer accountStatus; //账户状态
    private Date createTime; //账户创建时间
    private Date updateTime; //账户更新时间
}
