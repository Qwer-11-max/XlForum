package com.zxl.xlforum.account.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountSignupRequest extends AccountLoginRequest{
    @NotBlank
    @Size(min = 2,max = 20, message = "用户名应在4-20字符内")
    private String userName;
}
