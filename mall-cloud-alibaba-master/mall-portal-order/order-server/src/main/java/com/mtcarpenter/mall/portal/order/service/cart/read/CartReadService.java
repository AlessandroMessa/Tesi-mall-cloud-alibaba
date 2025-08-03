
package com.mtcarpenter.mall.portal.order.service.cart.read;

import com.mtcarpenter.mall.domain.CartPromotionItem;
import com.mtcarpenter.mall.model.OmsCartItem;
import com.mtcarpenter.mall.domain.cart.CartProduct;
import java.util.List;

public interface CartReadService {
    List<OmsCartItem> list(Long memberId);
    CartProduct getCartProduct(Long productId);
    List<CartPromotionItem> listPromotion(Long memberId, List<Long> cartIds);
}
