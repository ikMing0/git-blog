package com.czm.utils;

import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BeanCopyUtils {

    private BeanCopyUtils(){}

    /*这里设置泛型，让返回值返回的直接就是传进来的那个类型*/
    public static <V> V copyBen(Object source,Class<V> clazz){
        //创建目标对象
        V result = null;
        try {
            result = clazz.newInstance();
            //实现属性拷贝
            BeanUtils.copyProperties(source,result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //返回结果
        return result;
    }

    public static <O,V> List<V> copyBeanList(List<O> list,Class<V> clazz){
        return list.stream()
                .map(o -> copyBen(o, clazz))
                .collect(Collectors.toList());
    }
}
