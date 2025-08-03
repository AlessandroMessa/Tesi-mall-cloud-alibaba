package com.mtcarpenter.mall.portal.order.service.cart.write.impl;

import com.mtcarpenter.mall.model.OmsCartItem;
import com.mtcarpenter.mall.model.UmsMember;
import com.mtcarpenter.facade.cart.CartFacade;
import com.mtcarpenter.mall.portal.order.service.cart.write.CartWriteService;
import com.mtcarpenter.mall.util.MemberUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Service
public class CartWriteServiceImpl implements CartWriteService {

    @Autowired
    private CartFacade cartFacade;
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

        // Check existing item via facade
        OmsCartItem existCartItem = cartFacade.getExistingCartItem(cartItem);
        if (existCartItem == null) {
            cartItem.setCreateDate(new Date());
            return cartFacade.insertCartItem(cartItem);
        } else {
            cartItem.setModifyDate(new Date());
            existCartItem.setQuantity(existCartItem.getQuantity() + cartItem.getQuantity());
            return cartFacade.updateCartItem(existCartItem);
        }
    }

    @Override
    public int updateQuantity(Long id, Long memberId, Integer quantity) {
        return cartFacade.updateCartItemQuantity(id, memberId, quantity);
    }

    @Override
    public int delete(Long memberId, List<Long> ids) {
        return cartFacade.removeCartItems(memberId, ids);
    }

    @Override
    public int updateAttr(OmsCartItem cartItem) {
        // Mark existing as deleted
        OmsCartItem updateCart = new OmsCartItem();
        updateCart.setId(cartItem.getId());
        updateCart.setModifyDate(new Date());
        updateCart.setDeleteStatus(1);
        cartFacade.updateCartItem(updateCart);
        // Re-add with new attributes
        cartItem.setId(null);
        return add(cartItem);
    }

    @Override
    public int clear(Long memberId) {
        return cartFacade.clearCart(memberId);
    }
}
