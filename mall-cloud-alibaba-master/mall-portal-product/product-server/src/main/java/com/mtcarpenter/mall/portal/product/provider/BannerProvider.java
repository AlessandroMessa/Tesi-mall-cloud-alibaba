package com.mtcarpenter.mall.portal.product.provider;

import com.mtcarpenter.mall.client.advertise.AdvertiseClient;
import com.mtcarpenter.mall.model.home.SmsHomeAdvertise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BannerProvider {

    @Autowired
    private AdvertiseClient advertiseClient;

    public List<SmsHomeAdvertise> getHomeBanners() {
        return advertiseClient.getHomeAdvertiseList().getData();
    }
}
