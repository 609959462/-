package com.ppf.service.impl;

import com.google.common.collect.Lists;
import com.ppf.common.CheckEnum;
import com.ppf.common.ResponseCode;
import com.ppf.common.ServerResponse;
import com.ppf.dao.CartMapper;
import com.ppf.dao.ProductMapper;
import com.ppf.pojo.Cart;
import com.ppf.pojo.Product;
import com.ppf.service.ICart;
import com.ppf.service.IProductService;
import com.ppf.utils.BigDecimalUtils;
import com.ppf.vo.CartProductVO;
import com.ppf.vo.CartVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartServiceImpl implements ICart{
@Autowired
    CartMapper cartMapper;
@Autowired
    IProductService iProductService;
/**
 * 查看购物车
 * */
    @Override
    public ServerResponse cartList(Integer userid) {
        if(userid==null){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"请登录");
        }
        CartVO cartVO=getCartVO(userid);
        return ServerResponse.createServerResponseBySucess("查看成功",cartVO);
    }
    //添加商品
    public ServerResponse addProduct(Integer userId,Integer productId,Integer count){
        //参数非空校验
        if(productId==null||count==null){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"参数不能为空");
        }
        ServerResponse<Product> serverResponse=iProductService.findProductStock(productId);
        //判断商品以及库存是否存在
        if(!serverResponse.isSucess()){
            return ServerResponse.createServerResponseByFail(serverResponse.getStatus(),serverResponse.getMsg());
        }

        Product product=serverResponse.getData();

        if(product.getStock()<=0){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"售空");
        }
        //判断商品在购物车中是更新数量还是添加新商品By productId
        Cart cart=cartMapper.findProductByUserIdAndProduct(userId,productId);
        if(cart==null){//添加商品
            Cart cart1=new Cart();
            cart1.setUserId(userId);
             cart1.setProductId(productId);
            cart1.setChecked(CheckEnum.CHECK_CHECKE.getStatus());
            cart1.setQuantity(count);
            int result=cartMapper.insert(cart1);
            if(result<=0){
                return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"添加失败");
            }

        }else{//更新商品
            cart.setQuantity(cart.getQuantity()+count);
            int result=cartMapper.updateByPrimaryKey(cart);
            if (result<=0) {
                return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"更新失败");
            }
            }
            CartVO cartVO=getCartVO(userId);
            return ServerResponse.createServerResponseBySucess("成功",cartVO);
        }

    @Override
    public ServerResponse uodateProduct(Integer userId, Integer productId, Integer count) {
        if(productId==null||count==null){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"参数必传");
        }
        int result=cartMapper.updateByUserIdAndCount(userId,productId,count);
        if(result<=0){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"添加失败");
        }
        CartVO cartVO=getCartVO(userId);
        return ServerResponse.createServerResponseBySucess("添加成功",cartVO);
    }

    @Override
    public ServerResponse deleteProduct(Integer userId, Integer productId) {
        if(productId==null){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"参数必传");
        }
        int result=cartMapper.deleteByUserIdAndCount(userId,productId);
        if(result<=0){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"删除失败");
        }
        CartVO cartVO=getCartVO(userId);
        return ServerResponse.createServerResponseBySucess("删除成功",cartVO);
    }

    @Override
    public ServerResponse selectOneProduct(Integer userId, Integer productId) {
        if(productId==null){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"参数必传");
        }
        int result=cartMapper.updateByCheck(userId,productId);
        if(result<=0){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"勾选失败");
        }
        CartVO cartVO=getCartVO(userId);
        return ServerResponse.createServerResponseBySucess("勾选成功",cartVO);
    }

    @Override
    public ServerResponse unselect(Integer userId, Integer productId) {
        if(productId==null){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"参数必传");
        }
        int result=cartMapper.upselect(userId,productId);
        if(result<=0){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"取消勾选失败");
        }
        CartVO cartVO=getCartVO(userId);
        return ServerResponse.createServerResponseBySucess("取消勾选成功",cartVO);
    }

    @Override
    public ServerResponse selectAll(Integer userId) {
        int result=cartMapper.allSelect(userId);
        if(result<=0){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"全选失败");
        }
        CartVO cartVO=getCartVO(userId);
        return ServerResponse.createServerResponseBySucess("全选成功",cartVO);
    }

    @Override
    public ServerResponse unselectAll(Integer userId) {
        int result=cartMapper.unallSelect(userId);
        if(result<=0){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"取消全选失败");
        }
        CartVO cartVO=getCartVO(userId);
        return ServerResponse.createServerResponseBySucess("取消全选成功",cartVO);
    }
    @Override
    public ServerResponse get_cart_product_count(Integer userId) {

        int quantity=cartMapper.get_cart_product_count(userId);
        return ServerResponse.createServerResponseBySucess("",quantity);
    }

    /**
     * 核心
     * 封装
     *根据用户id获取cartvo--用户的状态--cart的其他内容都在此内
     */
    private CartVO getCartVO(Integer userId){
        CartVO cartVO=new CartVO();
         //根据id查购物信息
        List<Cart> cartList= cartMapper.selectByUserId(userId);
        //先CartProductVO
        if(cartList==null||cartList.size()==0){
            return cartVO;
        }

        String limitQuantity=null;
        int limit_quanlity=0;
        List<CartProductVO> productVOList=Lists.newArrayList();
        BigDecimal cartTotalPrice=new BigDecimal("0");
        for(Cart cart:cartList){
            CartProductVO cartProductVO=new CartProductVO();
            cartProductVO.setId(cart.getId());
            cartProductVO.setUserId(cart.getUserId());
            cartProductVO.setProductChecked(cart.getChecked());
            cartProductVO.setQuantity(cart.getQuantity());
            ServerResponse serverResponse= iProductService.findProductStock(cart.getProductId());
            if(serverResponse.isSucess()){
                Product product=(Product) serverResponse.getData();
                cartProductVO.setProductId(cart.getProductId());
                cartProductVO.setProductMainImage(product.getMainImage());
                cartProductVO.setProductName(product.getName());
                cartProductVO.setProductPrice(product.getPrice());
                cartProductVO.setProductStatus(product.getStatus());
                cartProductVO.setProductStock(product.getStock());
                cartProductVO.setProductSubtitle(product.getSubtitle());
                //商品总价格--封装BigDecimalUtils
                cartProductVO.setProductTotalPrice(BigDecimalUtils.mul(product.getPrice().doubleValue(),
                        cart.getQuantity()*1.0));
                //数量

                if(product.getStock()>=cart.getQuantity()){
                    limit_quanlity=cart.getQuantity();
                    limitQuantity="LIMIT_NUM_SUCCESS";
                }else{
                    limit_quanlity=product.getStock();
                    limitQuantity="LIMIT_NUM_FALL";
                }
                cartProductVO.setQuantity(limit_quanlity);
                cartProductVO.setLimitQuantity(limitQuantity);
                productVOList.add(cartProductVO);
                //计算购物车总价格(已选中的)
                //Bigdecimail转成double
                //商品价值累加
               if(cart.getChecked()==CheckEnum.CHECK_CHECKE.getStatus()){
                   cartTotalPrice=BigDecimalUtils.add(cartTotalPrice.doubleValue(),cartProductVO.getProductTotalPrice().doubleValue());
               }
            }
            //组合前端所要的vo
            cartVO.setCarttotalprice(cartTotalPrice);
            cartVO.setCartProductVOList(productVOList);
            int result=cartMapper.IsChecked(userId);
            if(result==0) {
                cartVO.setIsallchecked(true);
            }else {
                cartVO.setIsallchecked(false);
            }
        }
        //查询商品

        return cartVO;
}
    /**
     * 查看已经选择的购物车
     * */
    @Override
    public ServerResponse cartListChecked(Integer userid) {
        List<Cart> cartList=cartMapper.selectChecked(userid);
        return ServerResponse.createServerResponseBySucess("查看成功",cartList);
    }

    @Override
    public ServerResponse clearCart(List<Cart> cartList) {
        int result=cartMapper.clearCart(cartList);
        if(result!=cartList.size()){
            return ServerResponse.createServerResponseByFail(ResponseCode.ERROR,"删除失败");
        }
        return ServerResponse.createServerResponseBySucess();
    }
}