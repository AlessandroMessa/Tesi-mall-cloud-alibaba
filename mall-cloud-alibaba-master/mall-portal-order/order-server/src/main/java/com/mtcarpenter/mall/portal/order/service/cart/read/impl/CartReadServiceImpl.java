package com.mtcarpenter.mall.portal.order.service.cart.read.impl;

import cn.hutool.core.collection.CollUtil;
import com.mtcarpenter.mall.domain.CartPromotionItem;
import com.mtcarpenter.mall.domain.cart.CartProduct;
import com.mtcarpenter.mall.model.OmsCartItem;
import com.mtcarpenter.mall.portal.order.service.cart.read.CartReadService;
import com.mtcarpenter.provider.cart.CartItemDataProvider;
import com.mtcarpenter.provider.cart.ProductQueryProvider;
import com.mtcarpenter.provider.cart.PromotionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartReadServiceImpl implements CartReadService {
    @Autowired
    private ProductQueryProvider productQueryProvider;
    @Autowired
    private PromotionProvider promotionProvider;
    @Autowired
    private CartItemDataProvider cartItemDataProvider;


    @Override
    public List<CartPromotionItem> listPromotion(Long memberId, List<Long> cartIds) {
        List<OmsCartItem> cartItemList = list(memberId);
        if (CollUtil.isNotEmpty(cartIds)) {
            cartItemList = cartItemList.stream()
                    .filter(item -> cartIds.contains(item.getId()))
                    .collect(Collectors.toList());
        }
        if (CollectionUtils.isEmpty(cartItemList)) {
            return new ArrayList<>();
        }
        return promotionProvider.calculatePromotions(cartItemList);
    }
    @Override
    public List<OmsCartItem> list(Long memberId) {
        return cartItemDataProvider.listByMember(memberId);
    }
    @Override
    public CartProduct getCartProduct(Long productId) {
        return productQueryProvider.getCartProduct(productId);
    }

}
