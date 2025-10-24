package com.zxl.xlforum.comment.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentRequest{
    private String content; //用户评论
    private String postId; //帖子ID
    private String accountId; //用户ID
}