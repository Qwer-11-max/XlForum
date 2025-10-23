package com.zxl.xlforum.account.dto.req;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountBaseRequest {
    @Parameter(description = "用户邮箱，应唯一")
    @Email(message = "邮箱格式错误")
    private String email;
}
