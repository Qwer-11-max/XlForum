package com.zxl.xlforum.comment.controller;

import com.zxl.xlforum.comment.api.CommentApi;
import com.zxl.xlforum.comment.dto.req.CommentRequest;
import org.springframework.http.ResponseEntity;

public class CommentController implements CommentApi {

    @Override
    public ResponseEntity<?> addComment(CommentRequest commentRequest) {
        //todo 检查账号状态
        //todo 调用服务发表评论
        //todo 接受返回值，打包发往前端的数据
        return null;
    }

    @Override
    public ResponseEntity<?> searchCommentByUser(Integer accountId) {
        //todo 检查登录状态
        //todo 调用服务
        //todo 接受返回值，打包数据
        return null;
    }
}
