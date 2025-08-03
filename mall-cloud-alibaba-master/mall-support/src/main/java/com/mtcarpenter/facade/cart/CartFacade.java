package com.mtcarpenter.facade.cart;


import com.mtcarpenter.mall.domain.CartPromotionItem;
import com.mtcarpenter.mall.domain.cart.CartProduct;
import com.mtcarpenter.mall.model.OmsCartItem;
import com.mtcarpenter.provider.cart.CartItemDataProvider;
import com.mtcarpenter.provider.cart.ProductQueryProvider;
import com.mtcarpenter.provider.cart.PromotionProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Facade for cart-related data and operations, aggregating CartItemDataProvider,
 * ProductQueryProvider, and PromotionProvider to simplify client dependencies.
 */
@Service
public class CartFacade {

    @Autowired
    private CartItemDataProvider cartItemDataProvider;

    @Autowired
    private ProductQueryProvider productQueryProvider;

    @Autowired
    private PromotionProvider promotionProvider;

    /**
     * Retrieves an existing cart item for a member and product.
     */
    public OmsCartItem getExistingCartItem(OmsCartItem cartItem) {
        return cartItemDataProvider.getExistingCartItem(cartItem);
    }

    /**
     * Inserts a new cart item.
     */
    public int insertCartItem(OmsCartItem cartItem) {
        return cartItemDataProvider.insert(cartItem);
    }

    /**
     * Updates an entire cart item record.
     */
    public int updateCartItem(OmsCartItem cartItem) {
        return cartItemDataProvider.update(cartItem);
    }

    /**
     * Lists all cart items for a specific member.
     */
    public List<OmsCartItem> listCartItems(Long memberId) {
        return cartItemDataProvider.listByMember(memberId);
    }

    /**
     * Updates the quantity of a specific cart item for a member.
     */
    public int updateCartItemQuantity(Long id, Long memberId, Integer quantity) {
        return cartItemDataProvider.updateQuantity(id, memberId, quantity);
    }

    /**
     * Marks specified cart items as deleted for a member.
     */
    public int removeCartItems(Long memberId, List<Long> ids) {
        return cartItemDataProvider.markDeleted(memberId, ids);
    }

    /**
     * Clears all cart items for a member by marking them deleted.
     */
    public int clearCart(Long memberId) {
        return cartItemDataProvider.clearAll(memberId);
    }

    /**
     * Retrieves detailed product information for a cart.
     */
    public CartProduct getCartProduct(Long productId) {
        return productQueryProvider.getCartProduct(productId);
    }

    /**
     * Calculates promotions for a list of cart items.
     */
    public List<CartPromotionItem> calculatePromotions(List<OmsCartItem> items) {
        return promotionProvider.calculatePromotions(items);
    }
}

