package com.czm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czm.domain.ResponseResult;
import com.czm.domain.entity.Tag;
import com.czm.domain.vo.PageVo;
import com.czm.domain.vo.TagVo;
import com.czm.mapper.TagMapper;
import com.czm.service.TagService;
import com.czm.utils.BeanCopyUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 标签(Tag)表服务实现类
 *
 * @author makejava
 * @since 2022-12-04 15:02:40
 */
@Service("tagService")
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Override
    public ResponseResult<PageVo> pageTagList(Integer pageNum, Integer pageSize, String name, String remark) {
        //根据条件分页查询tag
        //条件
        LambdaQueryWrapper<Tag> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name),Tag::getName,name);
        wrapper.like(StringUtils.hasText(remark),Tag::getRemark,remark);
        //分页
        Page<Tag> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page(page,wrapper);
        //分装vo返回
        PageVo pageVo = new PageVo(page.getRecords(),page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult addTag(Tag tag) {
        System.out.println(tag);
        save(tag);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getOneTagById(Integer id) {
        Tag tag = getById(id);
        TagVo tagVo = BeanCopyUtils.copyBen(tag, TagVo.class);
        return ResponseResult.okResult(tagVo);
    }

    @Override
    public ResponseResult updateTagById(Tag tag) {
        LambdaUpdateWrapper<Tag> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Tag::getId,tag.getId());
        update(tag, wrapper);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult listAllTag() {
        //查询所有标签
        List<Tag> tags = list();
        List<TagVo> tagVos = BeanCopyUtils.copyBeanList(tags, TagVo.class);
        return ResponseResult.okResult(tagVos);
    }
}
