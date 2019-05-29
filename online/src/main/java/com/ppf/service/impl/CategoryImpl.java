package com.ppf.service.impl;

import com.google.common.collect.Sets;
import com.ppf.common.ResponseCode;
import com.ppf.common.ServerResponse;
import com.ppf.dao.CategoryMapper;
import com.ppf.pojo.Category;
import com.ppf.service.ICategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
public class CategoryImpl implements ICategory {
@Autowired
    CategoryMapper categoryMapper;



    @Override
    public ServerResponse add_category(Category category) {

        if(category.getName()==null){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"参数必传");
        }
        int result=categoryMapper.insert(category);
        if(result<=0){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"添加失败");
        }
        return ServerResponse.createServerResponseBySucess();
    }

    @Override
    public ServerResponse set_category_name(Category category) {
        if(category.getName()==null){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"参数必传");
        }
        int result=categoryMapper.updateByPrimaryKey(category);
        if(result<=0){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"修改失败");
        }
        return ServerResponse.createServerResponseBySucess("修改成功");
    }
/**
 * 递归查询
 * */
    @Override
    public ServerResponse get_deep_category(Category category) {
        if(category.getId()==null){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"id必传");
        }
        Set<Category> categorySet= Sets.newHashSet();
        Set<Category> set=findAllChild(categorySet,category.getId());
        /**
         * set是完整的数据，接口中要只有id的
         * */
        Set<Integer> ids=Sets.newHashSet();
        Iterator iterator=set.iterator();
        while (iterator.hasNext()){
           Category category1=(Category) iterator.next();
           ids.add(category1.getId());
        }
        return ServerResponse.createServerResponseBySucess(null,ids);
    }
/**
 * 先查是第几档的类---相当于查类别
 * */
    public Set<Category> findAllChild(Set<Category> categories,int categoryid){
        Category category=categoryMapper.selectByPrimaryKey(categoryid);
        //包含查的id这类
        if(category!=null){
            categories.add(category);
        }
        //categoryID的平级子类,递归子类的子类
        List<Category> categoryList=categoryMapper.selectById(categoryid);
        if(categoryList!=null&&categoryList.size()>0){
            for(Category category1:categoryList){
                findAllChild(categories,category1.getId());
            }
        }
        return categories;
    }
    /**
     *查询平级子节点
     * */
    public ServerResponse selectByParentId(Integer categoryId){
        if(categoryId==null){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"参数必须传");
        }
        List<Category> result=categoryMapper.selectById(categoryId);
        return ServerResponse.createServerResponseBySucess(null,result);
    }
}
