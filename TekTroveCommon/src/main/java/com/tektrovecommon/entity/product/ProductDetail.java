package com.tektrovecommon.entity.product;

import com.tektrovecommon.entity.IdBaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "product_details")
@Data
@NoArgsConstructor
public class ProductDetail extends IdBaseEntity {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String value;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public ProductDetail(int id, String name, String value, Product product) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.product = product;
    }

    public ProductDetail(String name, String value, Product product) {
        this.name = name;
        this.value = value;
        this.product = product;
    }
}
