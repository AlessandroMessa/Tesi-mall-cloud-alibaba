package com.mtcarpenter.mall.portal.order.service.lifecycle.impl;

import com.mtcarpenter.mall.common.exception.Asserts;
import com.mtcarpenter.mall.model.*;
import com.mtcarpenter.mall.model.order.OmsOrderExample;
import com.mtcarpenter.mall.model.order.OmsOrderItemExample;
import com.mtcarpenter.mall.portal.order.component.CancelOrderSender;
import com.mtcarpenter.mall.dao.PortalOrderDao;
import com.mtcarpenter.mall.domain.dto.OmsOrderDetail;
import com.mtcarpenter.mall.portal.order.service.lifecycle.OrderLifecycleService;
import com.mtcarpenter.facade.order.ProviderFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
@Slf4j
public class OrderLifecycleServiceImpl implements OrderLifecycleService {

    @Autowired
    private ProviderFacade providerFacade;

    @Autowired
    private PortalOrderDao portalOrderDao;

    @Autowired
    private CancelOrderSender cancelOrderSender;

    @Autowired
    private HttpServletRequest request;

    @Override
    public Integer cancelTimeOutOrder() {
        OmsOrderSetting setting = providerFacade.getOrderSettings().stream().findFirst().orElse(null);
        if (setting == null) return 0;

        List<OmsOrderDetail> timeOutOrders = portalOrderDao.getTimeOutOrders(setting.getNormalOrderOvertime());
        if (CollectionUtils.isEmpty(timeOutOrders)) return 0;

        List<Long> ids = new ArrayList<>();
        for (OmsOrderDetail order : timeOutOrders) ids.add(order.getId());
        portalOrderDao.updateOrderStatus(ids, 4);

        for (OmsOrderDetail order : timeOutOrders) {
            providerFacade.releaseStock(order.getOrderItemList());
            providerFacade.updateCouponStatus(order.getCouponId(), order.getMemberId(), 0);
            if (order.getUseIntegration() != null) {
                providerFacade.updateIntegration(order.getMemberId(), order.getUseIntegration());
            }
        }
        return timeOutOrders.size();
    }

    @Override
    public void cancelOrder(Long orderId) {
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andIdEqualTo(orderId).andStatusEqualTo(0).andDeleteStatusEqualTo(0);
        List<OmsOrder> orderList = providerFacade.selectOrdersByExample(example);
        if (CollectionUtils.isEmpty(orderList)) return;

        OmsOrder order = orderList.get(0);
        order.setStatus(4);
        providerFacade.updateOrder(order);

        OmsOrderItemExample itemExample = new OmsOrderItemExample();
        itemExample.createCriteria().andOrderIdEqualTo(orderId);
        List<OmsOrderItem> items = providerFacade.selectOrderItemsByExample(itemExample);

        if (!CollectionUtils.isEmpty(items)) {
            providerFacade.releaseStock(items);
        }
        providerFacade.updateCouponStatus(order.getCouponId(), order.getMemberId(), 0);
        if (order.getUseIntegration() != null) {
            providerFacade.updateIntegration(order.getMemberId(), order.getUseIntegration());
        }
    }

    @Override
    public void sendDelayMessageCancelOrder(Long orderId) {
        OmsOrderSetting setting = providerFacade.getOrderSettings().stream().findFirst().orElse(null);
        if (setting == null) return;

        long delay = setting.getNormalOrderOvertime() * 60L * 1000L;
        cancelOrderSender.sendMessage(orderId, delay);
    }

    @Override
    public void confirmReceiveOrder(Long orderId) {
        UmsMember member = providerFacade.getCurrentMember(request);
        OmsOrder order = providerFacade.selectOrderById(orderId);

        if (!member.getId().equals(order.getMemberId())) {
            Asserts.fail("不能确认他人订单！");
        }
        if (!Objects.equals(order.getStatus(), 2)) {
            Asserts.fail("该订单还未发货！");
        }
        order.setStatus(3);
        order.setConfirmStatus(1);
        order.setReceiveTime(new Date());
        providerFacade.updateOrder(order);
    }
}
