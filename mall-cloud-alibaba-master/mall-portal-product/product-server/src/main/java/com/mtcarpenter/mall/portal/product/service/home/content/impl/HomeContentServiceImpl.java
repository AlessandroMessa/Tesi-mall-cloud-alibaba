package com.mtcarpenter.mall.portal.product.service.home.content.impl;

import com.mtcarpenter.mall.client.advertise.AdvertiseClient;
import com.mtcarpenter.mall.client.coupon.promotion.FlashPromotionClient;
import com.mtcarpenter.mall.client.feign.SubjectFeign;
import com.mtcarpenter.mall.model.promotion.SmsFlashPromotion;
import com.mtcarpenter.mall.model.promotion.SmsFlashPromotionSession;
import com.mtcarpenter.mall.portal.product.dao.home.HomeDao;
import com.mtcarpenter.mall.portal.product.domain.home.HomeContentResult;
import com.mtcarpenter.mall.portal.product.domain.home.promotion.FlashPromotionProduct;
import com.mtcarpenter.mall.portal.product.domain.home.promotion.HomeFlashPromotion;
import com.mtcarpenter.mall.portal.product.service.home.content.HomeContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class HomeContentServiceImpl implements HomeContentService {

    @Autowired
    private HomeDao homeDao;
    @Autowired
    private SubjectFeign subjectFeign;
    @Autowired
    private AdvertiseClient advertiseClient;
    @Autowired
    private FlashPromotionClient flashPromotionClient;

    @Override
    public HomeContentResult content() {
        HomeContentResult result = new HomeContentResult();
        //获取首页广告
        result.setAdvertiseList(advertiseClient.getHomeAdvertiseList().getData());
        //获取推荐品牌
        result.setBrandList(homeDao.getRecommendBrandList(0,6));
        //获取秒杀信息
        result.setHomeFlashPromotion(getHomeFlashPromotion());
        //获取新品推荐
        result.setNewProductList(homeDao.getNewProductList(0, 4));
        //获取人气推荐
        result.setHotProductList(homeDao.getHotProductList(0, 4));
        //获取推荐专题
        result.setSubjectList(subjectFeign.getRecommendSubjectList(0, 4).getData());
        return result;
    }

    private HomeFlashPromotion getHomeFlashPromotion() {
        HomeFlashPromotion homeFlashPromotion = new HomeFlashPromotion();
        //获取当前秒杀活动
        Date now = new Date();
        SmsFlashPromotion flashPromotion = flashPromotionClient.getFlashPromotion(now).getData();
        if (flashPromotion != null) {
            //获取当前秒杀场次
            SmsFlashPromotionSession flashPromotionSession = flashPromotionClient.getFlashPromotionSession(now).getData();
            if (flashPromotionSession != null) {
                homeFlashPromotion.setStartTime(flashPromotionSession.getStartTime());
                homeFlashPromotion.setEndTime(flashPromotionSession.getEndTime());
                //获取下一个秒杀场次
                SmsFlashPromotionSession nextSession = flashPromotionClient.getNextFlashPromotionSession(homeFlashPromotion.getStartTime()).getData();
                if (nextSession != null) {
                    homeFlashPromotion.setNextStartTime(nextSession.getStartTime());
                    homeFlashPromotion.setNextEndTime(nextSession.getEndTime());
                }
                //获取秒杀商品
                List<FlashPromotionProduct> flashProductList = homeDao.getFlashProductList(flashPromotion.getId(), flashPromotionSession.getId());
                homeFlashPromotion.setProductList(flashProductList);
            }
        }
        return homeFlashPromotion;
    }
}
