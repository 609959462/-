package com.ppf.controller.backend;

import com.ppf.common.ServerResponse;
import com.ppf.pojo.Category;
import com.ppf.service.ICategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manage/category/")
public class CategerController {
    @Autowired
    ICategory categoryService;
/**
 * 获取子节点--查看
 * */
    @RequestMapping("get_category.do")
    public ServerResponse get_category(Integer id){

        return categoryService.selectByParentId(id);
    }
    /**
     * 增加节点
     * */
    @RequestMapping("add_category")
    public ServerResponse add_category(Category category){

        return categoryService.add_category(category);
    }
    /**
     * 修改品类
     * */
    @RequestMapping("set_category_name")
    public ServerResponse set_category_name(Category category){

        return categoryService.set_category_name(category);
    }
/**
 * 递归查询某个类别后的所有子类别
 * */
@RequestMapping("get_deep_category.do")
    public ServerResponse get_deep_category(Category category){

    return  categoryService.get_deep_category(category);
}
}
