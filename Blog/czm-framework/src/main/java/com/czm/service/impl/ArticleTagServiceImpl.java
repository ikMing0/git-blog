package com.czm.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.czm.domain.entity.ArticleTag;
import com.czm.mapper.ArticleTagMapper;
import com.czm.service.ArticleTagService;
import org.springframework.stereotype.Service;

/**
 * 文章标签关联表(ArticleTag)表服务实现类
 *
 * @author makejava
 * @since 2022-12-07 22:21:10
 */
@Service("articleTagService")
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagMapper, ArticleTag> implements ArticleTagService {

}
