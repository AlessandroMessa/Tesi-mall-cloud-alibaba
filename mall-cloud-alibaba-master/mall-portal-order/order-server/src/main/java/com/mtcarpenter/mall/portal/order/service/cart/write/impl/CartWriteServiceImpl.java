package com.mtcarpenter.mall.portal.order.service.cart.write.impl;

import com.mtcarpenter.mall.model.OmsCartItem;
import com.mtcarpenter.mall.model.UmsMember;
import com.mtcarpenter.mall.portal.order.service.cart.write.CartWriteService;
import com.mtcarpenter.mall.util.MemberUtil;
import com.mtcarpenter.provider.cart.CartItemDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Service
public class CartWriteServiceImpl implements CartWriteService {
    
    @Autowired
    private CartItemDataProvider cartItemDataProvider;
    @Autowired
    private MemberUtil memberUtil;
    @Autowired
    private HttpServletRequest request;

    @Override
    public int add(OmsCartItem cartItem) {
        UmsMember currentMember = memberUtil.getRedisUmsMember(request);
        cartItem.setMemberId(currentMember.getId());
        cartItem.setMemberNickname(currentMember.getNickname());
        cartItem.setDeleteStatus(0);

        OmsCartItem existCartItem = cartItemDataProvider.getExistingCartItem(cartItem);
        if (existCartItem == null) {
            cartItem.setCreateDate(new Date());
            return cartItemDataProvider.insert(cartItem);
        } else {
            cartItem.setModifyDate(new Date());
            existCartItem.setQuantity(existCartItem.getQuantity() + cartItem.getQuantity());
            return cartItemDataProvider.update(existCartItem);
        }
    }



    @Override
    public int updateQuantity(Long id, Long memberId, Integer quantity) {
        return cartItemDataProvider.updateQuantity(id, memberId, quantity);
    }

    @Override
    public int delete(Long memberId, List<Long> ids) {
        return cartItemDataProvider.markDeleted(memberId, ids);
    }


    @Override
    public int updateAttr(OmsCartItem cartItem) {
        OmsCartItem updateCart = new OmsCartItem();
        updateCart.setId(cartItem.getId());
        updateCart.setModifyDate(new Date());
        updateCart.setDeleteStatus(1);
        cartItemDataProvider.update(updateCart);
        cartItem.setId(null);
        add(cartItem);
        return 1;
    }

    @Override
    public int clear(Long memberId) {
        return cartItemDataProvider.clearAll(memberId);
    }
}
