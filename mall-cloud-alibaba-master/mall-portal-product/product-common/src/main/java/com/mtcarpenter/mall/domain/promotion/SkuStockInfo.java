package com.mtcarpenter.mall.domain.promotion;

import com.mtcarpenter.mall.model.PmsSkuStock;
import lombok.Data;

import java.util.List;

/**
 * Informazioni sull'inventario SKU del prodotto
 */
@Data
public class SkuStockInfo {
    private List<PmsSkuStock> skuStockList;
}
