package com.ppf.service;

import com.ppf.common.ServerResponse;
import com.ppf.pojo.Category;

public interface ICategory {
    public ServerResponse selectByParentId(Integer categoryId);
    public ServerResponse add_category(Category category);
    public ServerResponse set_category_name(Category category);
    public ServerResponse get_deep_category(Category category);

}
