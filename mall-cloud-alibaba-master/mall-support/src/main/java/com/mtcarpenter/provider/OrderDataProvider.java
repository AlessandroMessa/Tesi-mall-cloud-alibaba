package com.mtcarpenter.provider;

import com.mtcarpenter.mall.mapper.OmsOrderItemMapper;
import com.mtcarpenter.mall.mapper.OmsOrderMapper;
import com.mtcarpenter.mall.mapper.OmsOrderSettingMapper;
import com.mtcarpenter.mall.model.OmsOrder;
import com.mtcarpenter.mall.model.OmsOrderItem;
import com.mtcarpenter.mall.model.OmsOrderSetting;
import com.mtcarpenter.mall.model.order.OmsOrderExample;
import com.mtcarpenter.mall.model.order.OmsOrderItemExample;
import com.mtcarpenter.mall.model.order.OmsOrderSettingExample;
import com.mtcarpenter.mall.dao.PortalOrderDao;
import com.mtcarpenter.mall.dao.PortalOrderItemDao;
import com.mtcarpenter.mall.domain.dto.OmsOrderDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderDataProvider {

    @Autowired private OmsOrderMapper orderMapper;
    @Autowired private PortalOrderItemDao orderItemDao;
    @Autowired private OmsOrderSettingMapper orderSettingMapper;
    @Autowired
    private OmsOrderItemMapper orderItemMapper;
    @Autowired
    private PortalOrderDao portalOrderDao;

    public void insertOrder(OmsOrder order, List<OmsOrderItem> items) {
        orderMapper.insert(order);
        for (OmsOrderItem item : items) {
            item.setOrderId(order.getId());
            item.setOrderSn(order.getOrderSn());
        }
        orderItemDao.insertList(items);
    }

    public List<OmsOrderSetting> getOrderSettings() {
        return orderSettingMapper.selectByExample(new OmsOrderSettingExample());
    }

    public List<OmsOrder> selectOrdersByExample(OmsOrderExample example) {
        return orderMapper.selectByExample(example);
    }

    public List<OmsOrderItem> selectOrderItemsByExample(OmsOrderItemExample example) {
        return orderItemMapper.selectByExample(example);
    }
    public OmsOrder selectOrderById(Long id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    public void updateOrder(OmsOrder order) {
        orderMapper.updateByPrimaryKeySelective(order);
    }
    public OmsOrderDetail getOrderDetail(Long orderId) {
        return portalOrderDao.getDetail(orderId);
    }

    public int updateSkuStock(List<OmsOrderItem> items) {
        return portalOrderDao.updateSkuStock(items);
    }

}
