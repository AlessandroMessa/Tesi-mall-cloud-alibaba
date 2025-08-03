package com.mtcarpenter.mall.portal.order.service.payment.impl;

import com.mtcarpenter.mall.model.OmsOrder;
import com.mtcarpenter.mall.model.OmsOrderItem;
import com.mtcarpenter.mall.domain.dto.OmsOrderDetail;

import com.mtcarpenter.mall.portal.order.service.payment.OrderPaymentService;
import com.mtcarpenter.provider.OrderDataProvider;
import com.mtcarpenter.provider.StockProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OrderPaymentServiceImpl implements OrderPaymentService {

    @Autowired private OrderDataProvider orderDataProvider;
    @Autowired private StockProvider stockProvider;

    @Override
    public Integer paySuccess(Long orderId, Integer payType) {
        // 1. Aggiorna lo stato ordine a "pagato"
        OmsOrder order = new OmsOrder();
        order.setId(orderId);
        order.setStatus(1); // 已支付
        order.setPaymentTime(new Date());
        order.setPayType(payType);
        orderDataProvider.updateOrder(order);

        // 2. Recupera i dettagli dell'ordine (con item)
        OmsOrderDetail orderDetail = orderDataProvider.getOrderDetail(orderId);
        List<OmsOrderItem> orderItems = orderDetail.getOrderItemList();

        // 3. Conferma la modifica dello stock (sblocca e scala)
        return orderDataProvider.updateSkuStock(orderItems);
    }
}
