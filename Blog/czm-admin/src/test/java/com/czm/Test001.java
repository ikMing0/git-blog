package com.czm;

import com.czm.service.MenuService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class Test001 {

    @Autowired
    private MenuService menuService;

    @Test
    public void Test() {
        List<String> strings = menuService.selectPermsByUserId(6L);
        for (String s : strings){
            System.out.println(s);
        }
    }
}
