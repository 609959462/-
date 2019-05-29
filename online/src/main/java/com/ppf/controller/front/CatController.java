package com.ppf.controller.front;

import com.ppf.common.ServerResponse;
import com.ppf.pojo.User;
import com.ppf.service.ICart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/cart/")
public class CatController {
    @Autowired
    HttpSession session;
    @Autowired
    ICart cart;
    @RequestMapping("list.do")
     public ServerResponse cartList(){
        User user=(User)session.getAttribute("user");
        Integer userId=user.getId();
        return cart.cartList(userId);
    }
    @RequestMapping("add.do")
    public ServerResponse cartAdd(Integer productId,Integer count){
        User user=(User)session.getAttribute("user");
        Integer userId=user.getId();
       return cart.addProduct(userId,productId,count);
    }
    @RequestMapping("update.do")
    public ServerResponse updateQuan(Integer productId,Integer count){
        User user=(User)session.getAttribute("user");
        Integer userId=user.getId();
        return cart.uodateProduct(userId,productId,count);
    }
    @RequestMapping("delete_product.do")
    public ServerResponse deleteProduct(Integer productId){
        User user=(User)session.getAttribute("user");
        Integer userId=user.getId();
        return cart.deleteProduct(userId,productId);
    }
    @RequestMapping("select.do")
    public ServerResponse selectOneProduct(Integer productId){
        User user=(User)session.getAttribute("user");
        Integer userId=user.getId();
        return cart.selectOneProduct(userId,productId);
    }
    @RequestMapping("un_select.do")
    public ServerResponse unSelect(Integer productId){
        User user=(User)session.getAttribute("user");
        Integer userId=user.getId();
        return cart.unselect(userId,productId);
    }

    @RequestMapping("select_all.do")
    public ServerResponse selectAll(){
        User user=(User)session.getAttribute("user");
        Integer userId=user.getId();
        return cart.selectAll(userId);
    }
    @RequestMapping("un_select_all.do")
    public ServerResponse unSelectAll(){
        User user=(User)session.getAttribute("user");
        Integer userId=user.getId();
        return cart.unselectAll(userId);
    }
    @RequestMapping("get_cart_product_count.do")
    public ServerResponse get_cart_product_count(HttpSession session){

        User user=(User)session.getAttribute("user");
        Integer userId=user.getId();
        return cart.get_cart_product_count(userId);
    }
}
