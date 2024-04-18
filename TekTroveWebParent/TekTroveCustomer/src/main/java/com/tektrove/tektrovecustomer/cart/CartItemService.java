package com.tektrove.tektrovecustomer.cart;

import com.tektrove.tektrovecustomer.product.ProductRepository;
import com.tektrovecommon.entity.order.CartItem;
import com.tektrovecommon.entity.customer.Customer;
import com.tektrovecommon.entity.product.Product;
import com.tektrovecommon.exception.CartItemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartItemService(CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    public Integer addProduct(Integer customerId, Integer productId, Integer quantity) throws CartItemException {
        Optional<CartItem> cartItem = cartItemRepository.findByCustomer_IdAndProduct_Id(customerId, productId);
        if (cartItem.isEmpty()) {
            cartItemRepository.save(CartItem.builder()
                    .customer(new Customer(customerId))
                    .product(new Product(productId))
                    .quantity(quantity)
                    .build());
            return quantity;
        } else {
            int updateQuantity = cartItem.get().getQuantity() + quantity;
            if (updateQuantity > 5) {
                throw new CartItemException("You can't add more " + quantity + " items(s) " +
                        "because there's already " + cartItem.get().getQuantity()
                        + " item(s) in your shopping cart.");
            }
            cartItemRepository.updateQuantity(cartItem.get().getQuantity() + quantity, customerId, productId);
            return updateQuantity;
        }
    }

    public List<CartItem> listCartItems(Customer customer) {
        return cartItemRepository.findByCustomer(customer);
    }

    public float updateQuantity(Integer customerId, Integer productId, Integer quantity) {
        cartItemRepository.updateQuantity(quantity, customerId, productId);
        Product product = productRepository.findById(productId).get();
        return product.getDiscountPrice() * quantity;
    }

    public void removeProduct(Integer customerId, Integer productId) {
        cartItemRepository.deleteByCustomer_IdAndProduct_Id(customerId, productId);
    }
}
