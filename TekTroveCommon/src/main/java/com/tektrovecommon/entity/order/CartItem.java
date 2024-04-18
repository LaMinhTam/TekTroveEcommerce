package com.tektrovecommon.entity.order;

import com.tektrovecommon.entity.IdBaseEntity;
import com.tektrovecommon.entity.customer.Customer;
import com.tektrovecommon.entity.product.Product;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cart_items")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItem extends IdBaseEntity {
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private Integer quantity;

    @Transient
    public float getSubTotal() {
        return product.getDiscountPrice() * quantity;
    }
}
