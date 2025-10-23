package com.zxl.xlforum.account.dto.req;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountLoginRequest extends AccountBaseRequest{

    @Parameter(description = "用户密码")
    @Size(min = 6, max = 20, message = "密码应在6-20个字符内")
//    @Pattern(regexp = "^(?:(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])|(?=.*[0-9])(?=.*[a-z])(?=.*[^a-zA-Z0-9])|(?=.*[0-9])(?=.*[A-Z])(?=.*[^a-zA-Z0-9])|(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9])).{6,}$",
//            message = "密码需包含数字、大写字母、小写字母、特殊字符中至少三种，且长度≥6")
    private String password;
}
