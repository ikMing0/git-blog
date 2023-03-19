package com.czm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czm.constants.SystemConstants;
import com.czm.domain.ResponseResult;
import com.czm.domain.entity.Comment;
import com.czm.domain.vo.CommentVo;
import com.czm.domain.vo.PageVo;
import com.czm.enums.AppHttpCodeEnum;
import com.czm.exception.SystemException;
import com.czm.mapper.CommentMapper;
import com.czm.service.CommentService;
import com.czm.service.UserService;
import com.czm.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 评论表(Comment)表服务实现类
 *
 * @author makejava
 * @since 2022-11-27 14:15:17
 */
@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @Autowired
    private UserService userService;

    @Override
    public ResponseResult commentList(String commentType, String articleId, Integer pageNum, Integer pageSize) {
        //根据条件查询评论
        //根据文章id查询
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SystemConstants.ARTICLE_COMMENT.equals(commentType),Comment::getArticleId,articleId);
        //根据rootId查询
        queryWrapper.eq(Comment::getRootId,-1);
        //根据评论类型查询
        queryWrapper.eq(Comment::getType,commentType);
        //根据页码查询
        Page<Comment> page = new Page<>(pageNum,pageSize);
        page(page,queryWrapper);
        List<Comment> commentList = page.getRecords();
        //创建Vo，封装返回
        List<CommentVo> commentVos = getCommentListVos(commentList);


        //查找子评论
        commentVos = commentVos.stream()
                .map(commentVo -> commentVo.setChildren(getChildren(commentVo.getId())))
                .collect(Collectors.toList());
        return ResponseResult.okResult(new PageVo(commentVos, page.getTotal()));
    }

    @Override
    public ResponseResult addComment(Comment comment) {
        if (!StringUtils.hasText(comment.getContent())){
            throw new SystemException(AppHttpCodeEnum.CONTENT_NOT_NULL);
        }
        save(comment);
        return ResponseResult.okResult();

    }

    /**
     * 根据rootId查询其下的评论
     * @param Id
     * @return
     */
    private List<CommentVo> getChildren(Long Id){
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        //根据RootId的值，查询其下的评论
        queryWrapper.eq(Comment::getRootId,Id);
        List<Comment> children = list(queryWrapper);
        //需要封装成vo
        return getCommentListVos(children);
    }

    private List<CommentVo> getCommentListVos(List<Comment> list){
        return BeanCopyUtils.copyBeanList(list, CommentVo.class).stream()
                .map(new Function<CommentVo, CommentVo>() {
                    @Override
                    public CommentVo apply(CommentVo commentVo) {
                        //根据createBy查找username
                        String userName = userService.getById(commentVo.getCreateBy()).getUserName();
                        commentVo.setUsername(userName);
                        //根据toCommentUserId查找toCommentUserName
                        if (commentVo.getToCommentId()!=-1){
                            String toCommentUserName = userService.getById(commentVo.getToCommentUserId()).getUserName();
                            commentVo.setToCommentUserName(toCommentUserName);
                        }
                        return commentVo;
                    }
                }).collect(Collectors.toList());
    }
}
