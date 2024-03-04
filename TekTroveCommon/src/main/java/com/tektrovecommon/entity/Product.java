package com.tektrovecommon.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "products")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(unique = true, nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String alias;
    @Column(length = 1024, nullable = false)
    private String shortDescription;
    @Column(length = 4096, nullable = false)
    private String fullDescription;
    private Date createdTime;
    private Date updatedTime;
    private boolean enabled;
    private boolean inStock;
    private float cost;
    private float price;
    private float discountPercent;
    private float length;
    private float width;
    private float height;
    private float weight;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

}
