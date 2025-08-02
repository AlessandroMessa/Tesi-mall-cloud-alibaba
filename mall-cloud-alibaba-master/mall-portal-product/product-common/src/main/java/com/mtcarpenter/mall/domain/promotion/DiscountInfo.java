package com.mtcarpenter.mall.domain.promotion;

import com.mtcarpenter.mall.model.PmsProductFullReduction;
import com.mtcarpenter.mall.model.PmsProductLadder;
import lombok.Data;

import java.util.List;

/**
 * Informazioni sulle promozioni e sconti del prodotto
 */
@Data
public class DiscountInfo {
    private List<PmsProductLadder> productLadderList;           // Sconti per quantità
    private List<PmsProductFullReduction> productFullReductionList; // Sconti per soglia
}
