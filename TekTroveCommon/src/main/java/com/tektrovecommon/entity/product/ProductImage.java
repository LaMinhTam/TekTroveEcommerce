package com.tektrovecommon.entity.product;

import com.tektrovecommon.entity.IdBaseEntity;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "product_images")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImage extends IdBaseEntity {
    @Column(nullable = false)
    private String name;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Transient
    public String getImagePath() {
        return "/product-images/" + product.getId() + "/extras/" + name;
    }

    public ProductImage(int id, String name, Product product) {
        this.id = id;
        this.name = name;
        this.product = product;
    }

    @Override
    public String toString() {
        return name;
    }
}
