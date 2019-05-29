package com.ppf.service;

import com.ppf.common.ServerResponse;
import com.ppf.pojo.Cart;

import java.util.List;

public interface ICart {
    public ServerResponse cartList(Integer userid);
    public ServerResponse addProduct(Integer userId,Integer productId,Integer count);
    public ServerResponse uodateProduct(Integer userId,Integer productId,Integer count);
    public ServerResponse deleteProduct(Integer userId,Integer productId);
    public ServerResponse selectOneProduct(Integer userId,Integer productId);
    public ServerResponse unselect(Integer userId,Integer productId);
    public ServerResponse selectAll(Integer userId);
    public ServerResponse unselectAll(Integer userId);
    public ServerResponse get_cart_product_count(Integer userId);
    public ServerResponse cartListChecked(Integer userid);
    public ServerResponse clearCart(List<Cart> cartList);
}
