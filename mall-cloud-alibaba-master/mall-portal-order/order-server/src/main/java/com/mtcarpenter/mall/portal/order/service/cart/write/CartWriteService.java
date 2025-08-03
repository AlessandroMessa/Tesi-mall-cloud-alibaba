package com.mtcarpenter.mall.portal.order.service.cart.write;

import com.mtcarpenter.mall.model.OmsCartItem;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CartWriteService {
    @Transactional
    int add(OmsCartItem cartItem);

    int updateQuantity(Long id, Long memberId, Integer quantity);

    int delete(Long memberId, List<Long> ids);

    @Transactional
    int updateAttr(OmsCartItem cartItem);

    int clear(Long memberId);
}
