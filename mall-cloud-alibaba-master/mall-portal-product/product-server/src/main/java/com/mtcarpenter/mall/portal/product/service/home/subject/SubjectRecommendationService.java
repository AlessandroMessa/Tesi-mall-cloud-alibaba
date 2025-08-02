package com.mtcarpenter.mall.portal.product.service.home.subject;

import com.mtcarpenter.mall.model.CmsSubject;

import java.util.List;

public interface SubjectRecommendationService {
    /**
     * 根据专题分类分页获取专题
     *
     * @param cateId 专题分类id
     */
    List<CmsSubject> getSubjectList(Long cateId, Integer pageSize, Integer pageNum);


}
