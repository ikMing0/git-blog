package com.czm.runner;

import com.czm.domain.entity.Article;
import com.czm.mapper.ArticleMapper;
import com.czm.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class MyRunner implements CommandLineRunner {

    @Autowired
    ArticleMapper articleMapper;
    @Autowired
    RedisCache redisCache;

    @Override
    public void run(String... args) throws Exception {
        List<Article> articles = articleMapper.selectList(null);
        Map<String,Integer> viewCount = new HashMap();
        for (int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);
            viewCount.put(article.getId().toString(),article.getViewCount().intValue());
        }
        redisCache.setCacheMap("viewCount",viewCount);
        Map<String, Object> viewCounts = redisCache.getCacheMap("viewCount");
        System.out.println(viewCounts.size());
    }
}
