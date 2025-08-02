package com.mtcarpenter.mall.portal.product.service.home.content.provider;

import com.mtcarpenter.mall.client.feign.SubjectFeign;
import com.mtcarpenter.mall.model.CmsSubject;
import com.mtcarpenter.mall.model.PmsBrand;
import com.mtcarpenter.mall.model.PmsProduct;
import com.mtcarpenter.mall.portal.product.dao.home.HomeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RecommendationProvider {

    @Autowired
    private HomeDao homeDao;

    @Autowired
    private SubjectFeign subjectFeign;

    public List<PmsBrand> getRecommendBrands() {
        return homeDao.getRecommendBrandList(0, 6);
    }

    public List<PmsProduct> getNewProducts() {
        return homeDao.getNewProductList(0, 4);
    }

    public List<PmsProduct> getHotProducts() {
        return homeDao.getHotProductList(0, 4);
    }

    public List<CmsSubject> getSubjects() {
        return subjectFeign.getRecommendSubjectList(0, 4).getData();
    }
}
