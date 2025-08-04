package com.mtcarpenter.mall.domain.promotion;

import com.mtcarpenter.mall.PromotionRules;
import com.mtcarpenter.mall.model.PmsProduct;
import lombok.Data;

@Data
public class PromotionProduct {
    private PmsProduct product;
    private PromotionRules promotionRules;
}
