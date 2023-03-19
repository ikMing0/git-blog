package com.czm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czm.constants.SystemConstants;
import com.czm.domain.ResponseResult;
import com.czm.domain.entity.Link;
import com.czm.domain.vo.LinkVo;
import com.czm.domain.vo.PageVo;
import com.czm.mapper.LinkMapper;
import com.czm.service.LinkService;
import com.czm.utils.BeanCopyUtils;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 友链(Link)表服务实现类
 *
 * @author makejava
 * @since 2022-11-23 21:27:43
 */
@Service("linkService")
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {

    @Override
    public ResponseResult getAllLink() {
        //查询状态为通过的link
        LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Link::getStatus, SystemConstants.Link_STATUS_NORMAL);
        List<Link> links = list(queryWrapper);
        //封装vo
        List<LinkVo> linkVos = BeanCopyUtils.copyBeanList(links, LinkVo.class);
        //封装返回
        return ResponseResult.okResult(linkVos);
    }

    @Override
    public ResponseResult pageList(Long pageNum, Long pageSize, String name, String status) {
        //按条件分页查询link
        LambdaQueryWrapper<Link> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name),Link::getName,name);
        wrapper.eq(StringUtils.hasText(status),Link::getStatus,status);
        Page<Link> page = new Page<>(pageNum,pageSize);
        page(page,wrapper);
        return ResponseResult.okResult(new PageVo(page.getRecords(),page.getTotal()));
    }

    @Override
    public ResponseResult updateLinkById(Link link) {
        //根据id修改友链
        LambdaUpdateWrapper<Link> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Link::getId,link.getId());
        update(link,wrapper);
        return ResponseResult.okResult();
    }
}
