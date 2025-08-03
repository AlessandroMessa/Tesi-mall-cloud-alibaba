package com.mtcarpenter.provider.cart;

import com.mtcarpenter.mall.mapper.OmsCartItemMapper;
import com.mtcarpenter.mall.model.OmsCartItem;
import com.mtcarpenter.mall.model.cart.OmsCartItemExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartItemDataProvider {

    @Autowired
    private OmsCartItemMapper cartItemMapper;

    public OmsCartItem getExistingCartItem(OmsCartItem cartItem) {
        OmsCartItemExample example = new OmsCartItemExample();
        OmsCartItemExample.Criteria criteria = example.createCriteria()
                .andMemberIdEqualTo(cartItem.getMemberId())
                .andProductIdEqualTo(cartItem.getProductId())
                .andDeleteStatusEqualTo(0);
        if (cartItem.getProductSkuId() != null) {
            criteria.andProductSkuIdEqualTo(cartItem.getProductSkuId());
        }
        List<OmsCartItem> result = cartItemMapper.selectByExample(example);
        return result.isEmpty() ? null : result.get(0);
    }

    public int insert(OmsCartItem cartItem) {
        return cartItemMapper.insert(cartItem);
    }

    public int update(OmsCartItem cartItem) {
        return cartItemMapper.updateByPrimaryKey(cartItem);
    }

    public List<OmsCartItem> listByMember(Long memberId) {
        OmsCartItemExample example = new OmsCartItemExample();
        example.createCriteria().andDeleteStatusEqualTo(0).andMemberIdEqualTo(memberId);
        return cartItemMapper.selectByExample(example);
    }

    public int updateQuantity(Long id, Long memberId, Integer quantity) {
        OmsCartItem item = new OmsCartItem();
        item.setQuantity(quantity);
        OmsCartItemExample example = new OmsCartItemExample();
        example.createCriteria().andDeleteStatusEqualTo(0).andIdEqualTo(id).andMemberIdEqualTo(memberId);
        return cartItemMapper.updateByExampleSelective(item, example);
    }

    public int markDeleted(Long memberId, List<Long> ids) {
        OmsCartItem record = new OmsCartItem();
        record.setDeleteStatus(1);
        OmsCartItemExample example = new OmsCartItemExample();
        example.createCriteria().andIdIn(ids).andMemberIdEqualTo(memberId);
        return cartItemMapper.updateByExampleSelective(record, example);
    }

    public int clearAll(Long memberId) {
        OmsCartItem record = new OmsCartItem();
        record.setDeleteStatus(1);
        OmsCartItemExample example = new OmsCartItemExample();
        example.createCriteria().andMemberIdEqualTo(memberId);
        return cartItemMapper.updateByExampleSelective(record, example);
    }
}
