package com.czm.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.czm.domain.ResponseResult;
import com.czm.domain.entity.Category;
import com.czm.domain.vo.ExcelCategoryVo;
import com.czm.enums.AppHttpCodeEnum;
import com.czm.service.CategoryService;
import com.czm.utils.BeanCopyUtils;
import com.czm.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/content/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/listAllCategory")
    public ResponseResult listAllCategory(){
        //查询所有分类
        return categoryService.listAllCategory();
    }

    @GetMapping("/export")
    @PreAuthorize("@ps.hasPermission('content:category:export')")
    public void export(HttpServletResponse response){
        //导出ex文件
        // 这里注意 有同学反应使用swagger 会导致各种问题，请直接用浏览器或者用postman
        try {
            WebUtils.setDownLoadHeader("分类.xlsx",response);
            List<Category> categories = categoryService.list();
            List<ExcelCategoryVo> excelCategoryVos = BeanCopyUtils.copyBeanList(categories, ExcelCategoryVo.class);
            // 这里需要设置不关闭流
            EasyExcel.write(response.getOutputStream(), ExcelCategoryVo.class).autoCloseStream(Boolean.FALSE).sheet("分类导出")
                    .doWrite(excelCategoryVos);
        } catch (Exception e) {
            // 重置response
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
            WebUtils.renderString(response, JSON.toJSONString(result));
        }
    }

    @GetMapping("/list")
    public ResponseResult pageList(Long pageNum,Long pageSize,String name,String status){
        //分页查询分类列表
        return categoryService.pageList(pageNum,pageSize,name,status);
    }

    @PostMapping
    public ResponseResult addCategory(@RequestBody Category category){
        categoryService.save(category);
        return ResponseResult.okResult();
    }

    @GetMapping("{id}")
    public ResponseResult getOneById(@PathVariable Long id){
        return ResponseResult.okResult(categoryService.getById(id));
    }

    @PutMapping
    public ResponseResult updateCategoryById(@RequestBody Category category){
        return categoryService.updateCategoryById(category);
    }

    @DeleteMapping("{id}")
    public ResponseResult deleteById(@PathVariable Long id){
        categoryService.removeById(id);
        return ResponseResult.okResult();
    }
}
