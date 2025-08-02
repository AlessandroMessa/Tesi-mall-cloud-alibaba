package com.mtcarpenter.mall.portal.product.service.home.content.impl;

import com.mtcarpenter.mall.portal.product.domain.home.HomeContentResult;
import com.mtcarpenter.mall.portal.product.service.home.content.HomeContentService;
import com.mtcarpenter.mall.portal.product.provider.BannerProvider;
import com.mtcarpenter.mall.portal.product.provider.PromotionProvider;
import com.mtcarpenter.mall.portal.product.provider.RecommendationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HomeContentServiceImpl implements HomeContentService {

    private final BannerProvider bannerProvider;
    private final PromotionProvider promotionProvider;
    private final RecommendationProvider recommendationProvider;

    @Autowired
    public HomeContentServiceImpl(BannerProvider bannerProvider,
                                  PromotionProvider promotionProvider,
                                  RecommendationProvider recommendationProvider) {
        this.bannerProvider = bannerProvider;
        this.promotionProvider = promotionProvider;
        this.recommendationProvider = recommendationProvider;
    }

    @Override
    public HomeContentResult content() {
        HomeContentResult result = new HomeContentResult();
        result.setAdvertiseList(bannerProvider.getHomeBanners());
        result.setBrandList(recommendationProvider.getRecommendBrands());
        result.setHomeFlashPromotion(promotionProvider.getFlashPromotion());
        result.setNewProductList(recommendationProvider.getNewProducts());
        result.setHotProductList(recommendationProvider.getHotProducts());
        result.setSubjectList(recommendationProvider.getSubjects());
        return result;
    }
}