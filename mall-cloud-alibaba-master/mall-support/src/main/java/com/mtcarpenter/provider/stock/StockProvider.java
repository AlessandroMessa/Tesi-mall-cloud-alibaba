package com.mtcarpenter.provider.stock;

import com.mtcarpenter.mall.client.product.command.StockCommandClient;
import com.mtcarpenter.mall.domain.CartPromotionItem;
import com.mtcarpenter.mall.model.OmsOrderItem;
import com.mtcarpenter.mall.dao.PortalOrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockProvider {

    @Autowired
    private StockCommandClient stockCommandClient;
    @Autowired
    private PortalOrderDao portalOrderDao;

    public boolean hasStock(List<CartPromotionItem> items) {
        return items.stream().allMatch(i -> i.getRealStock() != null && i.getRealStock() > 0);
    }

    public void lockStock(List<CartPromotionItem> items) {
        for (CartPromotionItem item : items) {
            stockCommandClient.lockStock(item.getProductSkuId(), item.getQuantity());
        }
    }
    public void releaseStock(List<OmsOrderItem> items) {
        portalOrderDao.releaseSkuStockLock(items);
    }
}
