package com.mtcarpenter.mall.portal.product.service.home.product.impl;

import com.github.pagehelper.PageHelper;
import com.mtcarpenter.mall.mapper.PmsProductCategoryMapper;
import com.mtcarpenter.mall.mapper.PmsProductMapper;
import com.mtcarpenter.mall.model.PmsProduct;
import com.mtcarpenter.mall.model.PmsProductCategory;
import com.mtcarpenter.mall.model.product.PmsProductExample;
import com.mtcarpenter.mall.model.product.category.PmsProductCategoryExample;
import com.mtcarpenter.mall.portal.product.dao.home.HomeDao;
import com.mtcarpenter.mall.portal.product.service.home.product.ProductRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductRecommendationServiceImpl implements ProductRecommendationService {

    @Autowired
    private HomeDao homeDao;
    @Autowired
    private PmsProductMapper productMapper;
    @Autowired
    private PmsProductCategoryMapper productCategoryMapper;

    @Override
    public List<PmsProduct> recommendProductList(Integer pageSize, Integer pageNum) {
        // TODO: 2019/1/29 暂时默认推荐所有商品
        PageHelper.startPage(pageNum, pageSize);
        PmsProductExample example = new PmsProductExample();
        example.createCriteria()
                .andDeleteStatusEqualTo(0)
                .andPublishStatusEqualTo(1);
        return productMapper.selectByExample(example);
    }
    @Override
    public List<PmsProductCategory> getProductCateList(Long parentId) {
        PmsProductCategoryExample example = new PmsProductCategoryExample();
        example.createCriteria()
                .andShowStatusEqualTo(1)
                .andParentIdEqualTo(parentId);
        example.setOrderByClause("sort desc");
        return productCategoryMapper.selectByExample(example);
    }

    @Override
    public List<PmsProduct> hotProductList(Integer pageNum, Integer pageSize) {
        int offset = pageSize * (pageNum - 1);
        return homeDao.getHotProductList(offset, pageSize);
    }

    @Override
    public List<PmsProduct> newProductList(Integer pageNum, Integer pageSize) {
        int offset = pageSize * (pageNum - 1);
        return homeDao.getNewProductList(offset, pageSize);
    }
}
