package com.czm.domain.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CommentVo {

    private Long articleId;
    //评论内容
    private String content;

    private Long createBy;

    private Date createTime;
    //文章id
    private Long id;
    //根评论id
    private Long rootId;
    //回复目标评论id
    private Long toCommentId;
    private Long toCommentUserId;
    private String toCommentUserName;
    private String username;
    //子评论
    private List<CommentVo> children;
}
