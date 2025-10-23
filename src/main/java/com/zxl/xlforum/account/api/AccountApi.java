package com.zxl.xlforum.account.api;

import com.zxl.xlforum.account.dto.req.AccountBaseRequest;
import com.zxl.xlforum.account.dto.req.AccountLoginRequest;
import com.zxl.xlforum.account.dto.req.AccountSignupRequest;
import com.zxl.xlforum.account.dto.resp.AccountBaseResponse;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/")
@Validated
public interface AccountApi {

    public ResponseEntity<AccountBaseResponse> login(@Valid @RequestBody AccountLoginRequest accountLoginRequest);

    public ResponseEntity<AccountBaseResponse> signup(@Valid @RequestBody AccountSignupRequest accountSignupRequest);

    public ResponseEntity<AccountBaseResponse> changePassword(@Parameter(description = "用户密码")
                                                              @Size(min = 6, max = 20, message = "密码应在6-20个字符内")
                                                              @RequestParam
                                                              String oldPassword,
                                                              @Parameter(description = "用户密码")
                                                              @Size(min = 6, max = 20, message = "密码应在6-20个字符内")
                                                              @RequestParam
                                                              String newPassword);

    public ResponseEntity<AccountBaseResponse> forgetPassword(@Valid @RequestBody
                                                              AccountBaseRequest accountBaseRequest);

}
