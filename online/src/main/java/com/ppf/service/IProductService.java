package com.ppf.service;

import com.ppf.common.ServerResponse;
import com.ppf.pojo.Product;
import org.springframework.web.bind.annotation.RequestParam;

public interface IProductService {
    public ServerResponse addOrUpdate(Product product);
    public ServerResponse updateStatus(Product product);
    public ServerResponse search(String productName,
                                 Integer productId,
                                 Integer pageNum,
                                Integer pageSize);
    public ServerResponse detail(Integer productId);
    public ServerResponse findDetailByFront(Integer id);
    public ServerResponse searchFront(Integer categoryId,
                                     String keyword,
                                     Integer pageNum,
                                     Integer pageSize,
                                     String orderBy);
    public ServerResponse findProductStock(Integer id);
    public ServerResponse reduceStock(Integer id,Integer stock);
}
