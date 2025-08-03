package com.mtcarpenter.mall.portal.facade;

import com.mtcarpenter.mall.client.advertise.AdvertiseFeign;
import com.mtcarpenter.mall.client.promotion.PromotionSessionFeign;
import com.mtcarpenter.mall.model.SmsFlashPromotion;
import com.mtcarpenter.mall.model.SmsFlashPromotionSession;
import com.mtcarpenter.mall.model.SmsHomeAdvertise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class HomeContentFacade {
        @Autowired
        private AdvertiseFeign advertiseFeign;

        @Autowired
        private PromotionSessionFeign promotionSessionFeign;

        public List<SmsHomeAdvertise> getHomeAdvertiseList() {
            return advertiseFeign.getHomeAdvertiseList().getData();
        }

        public SmsFlashPromotion getFlashPromotion(Date date) {
            return promotionSessionFeign.getFlashPromotion(date).getData();
        }

        public SmsFlashPromotionSession getFlashPromotionSession(Date date) {
            return promotionSessionFeign.getFlashPromotionSession(date).getData();
        }

        public SmsFlashPromotionSession getNextFlashPromotionSession(Date date) {
            return promotionSessionFeign.getNextFlashPromotionSession(date).getData();
        }
}
