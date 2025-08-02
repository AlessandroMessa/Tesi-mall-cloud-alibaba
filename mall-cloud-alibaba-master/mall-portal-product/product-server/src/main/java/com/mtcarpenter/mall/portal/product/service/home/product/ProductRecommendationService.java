package com.mtcarpenter.mall.portal.product.service.home.product;

import com.mtcarpenter.mall.model.PmsProduct;
import com.mtcarpenter.mall.model.PmsProductCategory;

import java.util.List;

public interface ProductRecommendationService {
    /**
     * 首页商品推荐
     */
    List<PmsProduct> recommendProductList(Integer pageSize, Integer pageNum);
    /**
     * 分页获取人气推荐商品
     */
    List<PmsProduct> hotProductList(Integer pageNum, Integer pageSize);

    /**
     * 分页获取新品推荐商品
     */
    List<PmsProduct> newProductList(Integer pageNum, Integer pageSize);
    /**
     * 获取商品分类
     *
     * @param parentId 0:获取一级分类；其他：获取指定二级分类
     */
    List<PmsProductCategory> getProductCateList(Long parentId);
}
