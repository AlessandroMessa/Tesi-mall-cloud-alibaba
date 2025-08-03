package com.mtcarpenter.mall.portal.order.service.lifecycle.impl;

import com.mtcarpenter.mall.common.exception.Asserts;
import com.mtcarpenter.mall.model.*;
import com.mtcarpenter.mall.model.order.OmsOrderExample;
import com.mtcarpenter.mall.model.order.OmsOrderItemExample;
import com.mtcarpenter.mall.portal.order.component.CancelOrderSender;
import com.mtcarpenter.mall.dao.PortalOrderDao;
import com.mtcarpenter.mall.domain.dto.OmsOrderDetail;
import com.mtcarpenter.mall.portal.order.service.lifecycle.OrderLifecycleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@Slf4j
public class OrderLifecycleServiceImpl implements OrderLifecycleService {

    @Autowired private OrderDataProvider orderDataProvider;
    @Autowired private MemberProvider memberProvider;
    @Autowired private CouponProvider couponProvider;
    @Autowired private IntegrationProvider integrationProvider;
    @Autowired private StockProvider stockProvider;
    @Autowired private PortalOrderDao portalOrderDao;
    @Autowired private CancelOrderSender cancelOrderSender;
    @Autowired private HttpServletRequest request;

    @Override
    public Integer cancelTimeOutOrder() {
        Integer count = 0;
        OmsOrderSetting setting = orderDataProvider.getOrderSettings().stream().findFirst().orElse(null);
        if (setting == null) return 0;

        List<OmsOrderDetail> timeOutOrders = portalOrderDao.getTimeOutOrders(setting.getNormalOrderOvertime());
        if (CollectionUtils.isEmpty(timeOutOrders)) return count;

        List<Long> ids = new ArrayList<>();
        for (OmsOrderDetail order : timeOutOrders) {
            ids.add(order.getId());
        }
        portalOrderDao.updateOrderStatus(ids, 4);

        for (OmsOrderDetail order : timeOutOrders) {
            stockProvider.releaseStock(order.getOrderItemList());
            couponProvider.updateCouponStatus(order.getCouponId(), order.getMemberId(), 0);
            if (order.getUseIntegration() != null) {
                integrationProvider.updateIntegration(order.getMemberId(), order.getUseIntegration());
            }
        }
        return timeOutOrders.size();
    }

    @Override
    public void cancelOrder(Long orderId) {
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andIdEqualTo(orderId).andStatusEqualTo(0).andDeleteStatusEqualTo(0);
        List<OmsOrder> orderList = orderDataProvider.selectOrdersByExample(example);
        if (CollectionUtils.isEmpty(orderList)) return;

        OmsOrder order = orderList.get(0);
        order.setStatus(4);
        orderDataProvider.updateOrder(order);

        OmsOrderItemExample itemExample = new OmsOrderItemExample();
        itemExample.createCriteria().andOrderIdEqualTo(orderId);
        List<OmsOrderItem> items = orderDataProvider.selectOrderItemsByExample(itemExample);

        if (!CollectionUtils.isEmpty(items)) {
            stockProvider.releaseStock(items);
        }
        couponProvider.updateCouponStatus(order.getCouponId(), order.getMemberId(), 0);
        if (order.getUseIntegration() != null) {
            integrationProvider.updateIntegration(order.getMemberId(), order.getUseIntegration());
        }
    }

    @Override
    public void sendDelayMessageCancelOrder(Long orderId) {
        OmsOrderSetting setting = orderDataProvider.getOrderSettings().stream().findFirst().orElse(null);
        if (setting == null) return;

        long delay = setting.getNormalOrderOvertime() * 60L * 1000L;
        cancelOrderSender.sendMessage(orderId, delay);
    }

    @Override
    public void confirmReceiveOrder(Long orderId) {
        UmsMember member = memberProvider.getCurrentMember(request);
        OmsOrder order = orderDataProvider.selectOrderById(orderId);

        if (!member.getId().equals(order.getMemberId())) {
            Asserts.fail("不能确认他人订单！");
        }
        if (!Objects.equals(order.getStatus(), 2)) {
            Asserts.fail("该订单还未发货！");
        }
        order.setStatus(3);
        order.setConfirmStatus(1);
        order.setReceiveTime(new Date());
        orderDataProvider.updateOrder(order);
    }
}
