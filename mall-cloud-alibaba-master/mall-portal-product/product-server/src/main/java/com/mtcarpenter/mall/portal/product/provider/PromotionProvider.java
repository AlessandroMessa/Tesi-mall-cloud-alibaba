package com.mtcarpenter.mall.portal.product.provider;

import com.mtcarpenter.mall.client.coupon.promotion.FlashPromotionClient;
import com.mtcarpenter.mall.model.promotion.SmsFlashPromotion;
import com.mtcarpenter.mall.model.promotion.SmsFlashPromotionSession;
import com.mtcarpenter.mall.portal.product.dao.home.HomeDao;
import com.mtcarpenter.mall.portal.product.domain.home.promotion.FlashPromotionProduct;
import com.mtcarpenter.mall.portal.product.domain.home.promotion.HomeFlashPromotion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class PromotionProvider {

    @Autowired
    private FlashPromotionClient flashPromotionClient;
    @Autowired
    private HomeDao homeDao;

    public HomeFlashPromotion getFlashPromotion() {
        Date now = new Date();
        SmsFlashPromotion promotion = flashPromotionClient.getFlashPromotion(now).getData();
        if (promotion == null) return null;

        SmsFlashPromotionSession session = flashPromotionClient.getFlashPromotionSession(now).getData();
        if (session == null) return null;

        HomeFlashPromotion result = new HomeFlashPromotion();
        result.setStartTime(session.getStartTime());
        result.setEndTime(session.getEndTime());

        SmsFlashPromotionSession nextSession = flashPromotionClient
                .getNextFlashPromotionSession(session.getStartTime())
                .getData();

        if (nextSession != null) {
            result.setNextStartTime(nextSession.getStartTime());
            result.setNextEndTime(nextSession.getEndTime());
        }

        List<FlashPromotionProduct> products = homeDao.getFlashProductList(promotion.getId(), session.getId());
        result.setProductList(products);
        return result;
    }
}
