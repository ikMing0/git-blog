package com.czm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.czm.domain.ResponseResult;
import com.czm.domain.entity.Link;


/**
 * 友链(Link)表服务接口
 *
 * @author makejava
 * @since 2022-11-23 21:27:43
 */
public interface LinkService extends IService<Link> {

    ResponseResult getAllLink();

    ResponseResult pageList(Long pageNum, Long pageSize, String name, String status);

    ResponseResult updateLinkById(Link link);
}
