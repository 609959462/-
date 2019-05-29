package com.ppf.controller.front;

import com.ppf.common.ServerResponse;
import com.ppf.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
    @RequestMapping("/product/")
public class ProductFrontDetailController {
    @Autowired
    IProductService iProductService;
    /*
    * 前端商品详情
    * */
    @RequestMapping("detail.do")
    public ServerResponse detailFront(Integer productId){
        return iProductService.findDetailByFront(productId);
    }
    /**
     * 前台搜索商品
     * */
    @RequestMapping("list.do")
    public ServerResponse searchFront(@RequestParam(required = false)Integer categoryId,
                                      @RequestParam(required = false) String keyword,
                                      @RequestParam(required = false,defaultValue = "1")Integer pageNum,
                                      @RequestParam(required = false,defaultValue = "10")Integer pageSize,
                                      @RequestParam(required = false,defaultValue = "")String orderBy){
        return iProductService.searchFront(categoryId,keyword,pageNum,pageSize,orderBy);
    }
}
