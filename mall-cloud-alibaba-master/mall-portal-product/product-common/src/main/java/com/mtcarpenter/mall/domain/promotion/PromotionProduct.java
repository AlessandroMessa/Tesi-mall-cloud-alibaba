package com.mtcarpenter.mall.domain.promotion;

import com.mtcarpenter.mall.model.PmsProduct;
import lombok.Data;

/**
 * Rappresenta un prodotto con dati promozionali, ma separa le responsabilità
 */
@Data
public class PromotionProduct {
    private PmsProduct product;                // Dati base del prodotto
    private SkuStockInfo skuStockInfo;         // Dati inventario SKU
    private DiscountInfo discountInfo;         // Dati sconti
}
