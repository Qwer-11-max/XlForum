package com.zxl.xlforum.account.dto.resp;

import com.zxl.xlforum.common.resp.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper = true)
public class AccountBaseResponse extends BaseResponse {
    private String email; //用户邮箱
    private String userName; //用户名称
    private String userId; //用户ID
    private String userStatus; //用户状态
}
