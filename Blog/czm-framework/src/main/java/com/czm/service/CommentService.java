package com.czm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.czm.domain.ResponseResult;
import com.czm.domain.entity.Comment;


/**
 * 评论表(Comment)表服务接口
 *
 * @author makejava
 * @since 2022-11-27 14:15:17
 */
public interface CommentService extends IService<Comment> {

    ResponseResult commentList(String commentType, String articleId, Integer pageNum, Integer pageSize);

    ResponseResult addComment(Comment comment);
}
