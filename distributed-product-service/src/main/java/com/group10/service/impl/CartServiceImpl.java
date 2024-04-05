package com.group10.service.impl;

import com.alibaba.fastjson.JSON;
import com.group10.constants.CacheKey;
import com.group10.enums.BizCodeEnum;
import com.group10.exception.BizException;
import com.group10.interceptor.LoginInterceptor;
import com.group10.model.LoginUser;
import com.group10.request.CartItemRequest;
import com.group10.service.CartService;
import com.group10.service.ProductService;
import com.group10.vo.CartItemVO;
import com.group10.vo.CartVO;
import com.group10.vo.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@Slf4j
public class CartServiceImpl implements CartService {

    @Autowired
    private ProductService productService;

    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public void addToCart(CartItemRequest cartItemRequest) {

        long productId = cartItemRequest.getProductId();
        int buyNum = cartItemRequest.getBuyNum();

        BoundHashOperations<String,Object,Object> myCart =  getMyCartOps();

        Object cacheObj = myCart.get(productId);
        String result = "";

        if(cacheObj!=null){
           result =  (String)cacheObj;
        }

        if(StringUtils.isBlank(result)){
            CartItemVO cartItemVO = new CartItemVO();

            ProductVO productVO = productService.findDetailById(productId);
            if(productVO == null){throw new BizException(BizCodeEnum.CART_FAIL);}

            cartItemVO.setAmount(productVO.getAmount());
            cartItemVO.setBuyNum(buyNum);
            cartItemVO.setProductId(productId);
            cartItemVO.setProductImg(productVO.getCoverImg());
            cartItemVO.setProductTitle(productVO.getTitle());
            myCart.put(productId,JSON.toJSONString(cartItemVO));

        }else {
            CartItemVO cartItem = JSON.parseObject(result,CartItemVO.class);
            cartItem.setBuyNum(cartItem.getBuyNum()+buyNum);
            myCart.put(productId,JSON.toJSONString(cartItem));
        }

    }


    /**
     * empty cart
     */
    @Override
    public void clear() {
        String cartKey = getCartKey();
        redisTemplate.delete(cartKey);

    }


    /**
     * delete items
     * @param productId
     */
    @Override
    public void deleteItem(long productId) {

        BoundHashOperations<String,Object,Object> mycart =  getMyCartOps();

        mycart.delete(productId);

    }



    @Override
    public void changeItemNum(CartItemRequest cartItemRequest) {
        BoundHashOperations<String,Object,Object> mycart =  getMyCartOps();

        Object cacheObj = mycart.get(cartItemRequest.getProductId());

        if(cacheObj==null){throw new BizException(BizCodeEnum.CART_FAIL);}

        String obj = (String)cacheObj;

        CartItemVO cartItemVO =  JSON.parseObject(obj,CartItemVO.class);
        cartItemVO.setBuyNum(cartItemRequest.getBuyNum());
        mycart.put(cartItemRequest.getProductId(),JSON.toJSONString(cartItemVO));
    }

    @Override
    public List<CartItemVO> confirmOrderCartItems(List<Long> productIdList) {

        List<CartItemVO> cartItemVOList =  buildCartItem(true);

        List<CartItemVO> resultList =  cartItemVOList.stream().filter(obj->{

            if(productIdList.contains(obj.getProductId())){
                this.deleteItem(obj.getProductId());
                return true;
            }
            return false;

        }).collect(Collectors.toList());

        return resultList;
    }



    @Override
    public CartVO getMyCart() {

        List<CartItemVO> cartItemVOList = buildCartItem(false);

        CartVO cartVO = new CartVO();
        cartVO.setCartItems(cartItemVOList);

        return cartVO;
    }


    /**
     * get the latest shopping items,
     * @param latestPrice whether to get the latest price
     * @return
     */
    private List<CartItemVO> buildCartItem(boolean latestPrice) {

        BoundHashOperations<String,Object,Object> myCart = getMyCartOps();

        List<Object> itemList = myCart.values();

        List<CartItemVO> cartItemVOList = new ArrayList<>();

        List<Long> productIdList = new ArrayList<>();

        for(Object item: itemList){
            CartItemVO cartItemVO = JSON.parseObject((String)item,CartItemVO.class);
            cartItemVOList.add(cartItemVO);

            productIdList.add(cartItemVO.getProductId());
        }

        if(latestPrice){

            setProductLatestPrice(cartItemVOList,productIdList);
        }

        return cartItemVOList;

    }

    /**
     * set the lasted price of the item
     * @param cartItemVOList
     * @param productIdList
     */
    private void setProductLatestPrice(List<CartItemVO> cartItemVOList, List<Long> productIdList) {

        List<ProductVO> productVOList = productService.findProductsByIdBatch(productIdList);

        Map<Long,ProductVO> maps = productVOList.stream().collect(Collectors.toMap(ProductVO::getId,Function.identity()));


        cartItemVOList.stream().forEach(item->{

            ProductVO productVO = maps.get(item.getProductId());
            item.setProductTitle(productVO.getTitle());
            item.setProductImg(productVO.getCoverImg());
            item.setAmount(productVO.getAmount());

        });


    }


    /**
     * extract my cart, common method
     * @return
     */
    private BoundHashOperations<String,Object,Object> getMyCartOps(){
        String cartKey = getCartKey();
        return redisTemplate.boundHashOps(cartKey);
    }


    /**
     * cart key
     * @return
     */
    private String getCartKey(){
        LoginUser loginUser = LoginInterceptor.threadLocal.get();
        String cartKey = String.format(CacheKey.CART_KEY,loginUser.getId());
        return cartKey;

    }


}
