package com.mtcarpenter.mall.portal.product.service.home.subject.impl;

import com.github.pagehelper.PageHelper;
import com.mtcarpenter.mall.mapper.CmsSubjectMapper;
import com.mtcarpenter.mall.model.CmsSubject;
import com.mtcarpenter.mall.model.subject.CmsSubjectExample;
import com.mtcarpenter.mall.portal.product.service.home.subject.SubjectRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectRecommendationServiceImpl implements SubjectRecommendationService {
    @Autowired
    private CmsSubjectMapper subjectMapper;

    @Override
    public List<CmsSubject> getSubjectList(Long cateId, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        CmsSubjectExample example = new CmsSubjectExample();
        CmsSubjectExample.Criteria criteria = example.createCriteria();
        criteria.andShowStatusEqualTo(1);
        if (cateId != null) {
            criteria.andCategoryIdEqualTo(cateId);
        }
        return subjectMapper.selectByExample(example);
    }

}
