package com.mtcarpenter.mall.portal.order.service.query.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.github.pagehelper.PageHelper;
import com.mtcarpenter.mall.common.api.CommonPage;
import com.mtcarpenter.mall.common.exception.Asserts;
import com.mtcarpenter.mall.model.*;
import com.mtcarpenter.mall.model.order.OmsOrderExample;
import com.mtcarpenter.mall.model.order.OmsOrderItemExample;
import com.mtcarpenter.mall.domain.dto.OmsOrderDetail;

import com.mtcarpenter.mall.portal.order.service.query.OrderQueryService;
import com.mtcarpenter.provider.member.MemberProvider;
import com.mtcarpenter.provider.order.OrderDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderQueryServiceImpl implements OrderQueryService {

    @Autowired private MemberProvider memberProvider;
    @Autowired private OrderDataProvider orderDataProvider;
    @Autowired private HttpServletRequest request;

    @Override
    public CommonPage<OmsOrderDetail> list(Integer status, Integer pageNum, Integer pageSize) {
        UmsMember member = memberProvider.getCurrentMember(request);
        PageHelper.startPage(pageNum, pageSize);

        OmsOrderExample orderExample = new OmsOrderExample();
        OmsOrderExample.Criteria criteria = orderExample.createCriteria();
        criteria.andDeleteStatusEqualTo(0).andMemberIdEqualTo(member.getId());
        if (status != null && status != -1) {
            criteria.andStatusEqualTo(status);
        }
        orderExample.setOrderByClause("create_time desc");

        List<OmsOrder> orderList = orderDataProvider.selectOrdersByExample(orderExample);
        CommonPage<OmsOrder> orderPage = CommonPage.restPage(orderList);

        CommonPage<OmsOrderDetail> resultPage = new CommonPage<>();
        resultPage.setPageNum(orderPage.getPageNum());
        resultPage.setPageSize(orderPage.getPageSize());
        resultPage.setTotal(orderPage.getTotal());
        resultPage.setTotalPage(orderPage.getTotalPage());

        if (CollUtil.isEmpty(orderList)) {
            return resultPage;
        }

        List<Long> orderIds = new ArrayList<>();
        for (OmsOrder order : orderList) {
            orderIds.add(order.getId());
        }

        OmsOrderItemExample itemExample = new OmsOrderItemExample();
        itemExample.createCriteria().andOrderIdIn(orderIds);
        List<OmsOrderItem> orderItemList = orderDataProvider.selectOrderItemsByExample(itemExample);

        List<OmsOrderDetail> orderDetailList = new ArrayList<>();
        for (OmsOrder order : orderList) {
            OmsOrderDetail detail = new OmsOrderDetail();
            BeanUtil.copyProperties(order, detail);
            List<OmsOrderItem> relatedItems = new ArrayList<>();
            for (OmsOrderItem item : orderItemList) {
                if (item.getOrderId().equals(order.getId())) {
                    relatedItems.add(item);
                }
            }
            detail.setOrderItemList(relatedItems);
            orderDetailList.add(detail);
        }

        resultPage.setList(orderDetailList);
        return resultPage;
    }

    @Override
    public OmsOrderDetail detail(Long orderId) {
        OmsOrder order = orderDataProvider.selectOrderById(orderId);
        OmsOrderItemExample example = new OmsOrderItemExample();
        example.createCriteria().andOrderIdEqualTo(orderId);
        List<OmsOrderItem> itemList = orderDataProvider.selectOrderItemsByExample(example);
        OmsOrderDetail detail = new OmsOrderDetail();
        BeanUtil.copyProperties(order, detail);
        detail.setOrderItemList(itemList);
        return detail;
    }

    @Override
    public void deleteOrder(Long orderId) {
        UmsMember member = memberProvider.getCurrentMember(request);
        OmsOrder order = orderDataProvider.selectOrderById(orderId);
        if (!member.getId().equals(order.getMemberId())) {
            Asserts.fail("不能删除他人订单！");
        }
        if (order.getStatus() == 3 || order.getStatus() == 4) {
            order.setDeleteStatus(1);
            orderDataProvider.updateOrder(order);
        } else {
            Asserts.fail("只能删除已完成或已关闭的订单！");
        }
    }
}
