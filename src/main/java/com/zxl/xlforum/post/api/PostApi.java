package com.zxl.xlforum.post.api;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/post")
@Validated
public interface PostApi {

    /**
     * 输入标题，内容和账号id，从而发布帖子
     * @param title
     * @param content
     * @param accountId
     * @return
     */
    @Operation(description = "发帖")
    @GetMapping("/addPost")
    ResponseEntity<?> addPost(String title, String content, Integer accountId);

    /**
     * 输入账号查询账号发过的帖子
     * @param accountId
     * @return
     */
    @Operation(description = "用户查询自己发过的帖子")
    @GetMapping("/searchPostByAccount")
    ResponseEntity<?> searchPostByAccount(Integer accountId);

    /**
     * 通过标题查询帖子
     * @param title
     * @return
     */
    @Operation(description = "通过标题搜索帖子")
    @GetMapping("/searchPostByTitle")
    ResponseEntity<?> searchPostByTitle(String title);
}