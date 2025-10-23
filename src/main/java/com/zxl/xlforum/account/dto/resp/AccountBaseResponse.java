package com.zxl.xlforum.account.dto.resp;

import lombok.Data;

@Data
public class AccountBaseResponse {
    private String email; //用户邮箱
    private String userName; //用户名称
    private String userId; //用户ID
    private String userStatus; //用户状态
}
