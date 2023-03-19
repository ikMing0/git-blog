package com.czm.domain.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagVo {
    private Long id;
    private String name;
    //备注
    private String remark;
}
