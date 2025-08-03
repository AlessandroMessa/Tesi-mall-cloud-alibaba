package com.mtcarpenter.provider;

import com.mtcarpenter.mall.client.integration.IntegrationSettingQueryClient;
import com.mtcarpenter.mall.client.member.command.MemberIntegrationCommandClient;
import com.mtcarpenter.mall.model.UmsIntegrationConsumeSetting;
import com.mtcarpenter.mall.model.UmsMember;
import com.mtcarpenter.mall.model.OmsOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class IntegrationProvider {

    @Autowired
    private IntegrationSettingQueryClient integrationSettingQueryClient;
    @Autowired
    private MemberIntegrationCommandClient memberIntegrationCommandClient;

    public UmsIntegrationConsumeSetting getConsumeSetting() {
        return integrationSettingQueryClient.integrationConsumeSetting(1L).getData();
    }

    public void updateIntegration(Long memberId, int delta) {
        memberIntegrationCommandClient.updateIntegration(memberId, delta);
    }

    public BigDecimal getUseIntegrationAmount(Integer useIntegration, BigDecimal totalAmount, UmsMember member, boolean hasCoupon) {
        if (useIntegration > member.getIntegration()) return BigDecimal.ZERO;

        UmsIntegrationConsumeSetting setting = getConsumeSetting();

        if (hasCoupon && setting.getCouponStatus().equals(0)) return BigDecimal.ZERO;
        if (useIntegration < setting.getUseUnit()) return BigDecimal.ZERO;

        BigDecimal integrationAmount = new BigDecimal(useIntegration)
                .divide(new BigDecimal(setting.getUseUnit()), 2, RoundingMode.HALF_EVEN);

        BigDecimal maxPercent = new BigDecimal(setting.getMaxPercentPerOrder())
                .divide(new BigDecimal(100), 2, RoundingMode.HALF_EVEN);

        return integrationAmount.compareTo(totalAmount.multiply(maxPercent)) > 0
                ? BigDecimal.ZERO : integrationAmount;
    }

    public BigDecimal calcIntegrationAmount(List<OmsOrderItem> items) {
        return items.stream()
                .filter(i -> i.getIntegrationAmount() != null)
                .map(i -> i.getIntegrationAmount().multiply(BigDecimal.valueOf(i.getProductQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
