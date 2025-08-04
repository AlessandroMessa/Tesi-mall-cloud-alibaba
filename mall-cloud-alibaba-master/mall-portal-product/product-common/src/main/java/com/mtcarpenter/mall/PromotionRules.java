package com.mtcarpenter.mall;

import com.mtcarpenter.mall.model.PmsProductFullReduction;
import com.mtcarpenter.mall.model.PmsProductLadder;
import com.mtcarpenter.mall.model.PmsSkuStock;
import lombok.Data;

import java.util.List;

@Data
public class PromotionRules {
    private List<PmsSkuStock> skuStockList;
    private List<PmsProductLadder> productLadderList;
    private List<PmsProductFullReduction> productFullReductionList;
}
