package com.czm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.czm.domain.entity.Article;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

}
