package com.czm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.czm.domain.ResponseResult;
import com.czm.domain.entity.Tag;
import com.czm.domain.vo.PageVo;


/**
 * 标签(Tag)表服务接口
 *
 * @author makejava
 * @since 2022-12-04 15:02:40
 */
public interface TagService extends IService<Tag> {

    ResponseResult<PageVo> pageTagList(Integer pageNum, Integer pageSize, String name, String remark);

    ResponseResult addTag(Tag tag);

    ResponseResult getOneTagById(Integer id);

    ResponseResult updateTagById(Tag tag);

    ResponseResult listAllTag();

}
