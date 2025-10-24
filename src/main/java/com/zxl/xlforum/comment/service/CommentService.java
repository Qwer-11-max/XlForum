package com.zxl.xlforum.comment.service;

import com.zxl.xlforum.comment.dto.req.CommentRequest;
import com.zxl.xlforum.comment.dto.resp.CommentResponse;
import org.springframework.stereotype.Service;

@Service
public class CommentService {
    public CommentResponse addComment(CommentRequest commentRequest) {
        //todo 向数据库中插入
        return null;
    }

    public CommentResponse searchCommentByUser(Integer accountId) {
        //todo 从数据库中取出所需要的数据
        return null;
    }
}
