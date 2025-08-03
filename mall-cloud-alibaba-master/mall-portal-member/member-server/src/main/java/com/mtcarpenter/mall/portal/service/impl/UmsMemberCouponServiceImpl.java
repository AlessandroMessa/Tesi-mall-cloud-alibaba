package com.mtcarpenter.mall.portal.service.impl;


import com.mtcarpenter.mall.domain.SmsCouponHistoryDetail;
import com.mtcarpenter.mall.model.SmsCoupon;
import com.mtcarpenter.mall.model.SmsCouponHistory;
import com.mtcarpenter.mall.model.UmsMember;
import com.mtcarpenter.mall.portal.facade.CouponClientFacade;
import com.mtcarpenter.mall.portal.service.UmsMemberCouponService;
import com.mtcarpenter.mall.portal.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 会员优惠券管理Service实现类
 * Created by macro on 2018/8/29.
 */
@Service
public class UmsMemberCouponServiceImpl implements UmsMemberCouponService {
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private CouponClientFacade couponClientFacade;

    @Override
    public void add(Long couponId) {
        UmsMember currentMember = memberService.getCurrentMember();
        couponClientFacade.addCoupon(couponId, currentMember.getId(), currentMember.getNickname());
    }


    @Override
    public List<SmsCoupon> list(Integer useStatus) {
        UmsMember currentMember = memberService.getCurrentMember();
        return couponClientFacade.listCoupons(currentMember.getId(), useStatus);
    }

    @Override
    public List<SmsCouponHistoryDetail> listCart(Integer type) {
        UmsMember currentMember = memberService.getCurrentMember();
        return couponClientFacade.listCartCoupons(type, currentMember.getId());
    }

    @Override
    public List<SmsCouponHistory> listHistory(Integer useStatus) {
        UmsMember currentMember = memberService.getCurrentMember();
        return couponClientFacade.listCouponHistory(currentMember.getId(), useStatus);
    }



}
