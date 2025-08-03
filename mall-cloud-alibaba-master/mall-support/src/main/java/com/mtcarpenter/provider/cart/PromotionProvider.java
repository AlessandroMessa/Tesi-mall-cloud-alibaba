package com.mtcarpenter.provider.cart;

import com.mtcarpenter.mall.domain.CartPromotionItem;
import com.mtcarpenter.mall.model.OmsCartItem;
import com.mtcarpenter.mall.service.OmsPromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PromotionProvider {

    @Autowired
    private OmsPromotionService promotionService;

    public List<CartPromotionItem> calculatePromotions(List<OmsCartItem> items) {
        return promotionService.calcCartPromotion(items);
    }
}
