package com.zxl.xlforum.comment.api;

import com.zxl.xlforum.comment.dto.req.CommentRequest;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController("/comment")
@Validated
public interface CommentApi {

    /**
     * 发评论
     * @param commentRequest
     * @return 更新后的评论
     */
    @Operation(description = "发评论")
    @GetMapping("/addComment")
    ResponseEntity<?> addComment(@Valid @RequestBody CommentRequest commentRequest);

    /**
     * 用户查询自己发过的评论
     * @param accountId
     * @return 评论列表
     */
    @Operation(description = "用户查询自己发过的评论")
    @GetMapping("/searchCommentByUser")
    ResponseEntity<?> searchCommentByUser(@NotBlank @RequestParam Integer accountId);
}


