package com.tektrovecommon.entity.product;

import com.tektrovecommon.entity.Brand;
import com.tektrovecommon.entity.Category;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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
    @Column(nullable = false)
    private String mainImage;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductImage> images = new HashSet<>();
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductDetail> details = new ArrayList<>();

    public void addExtraImage(String imageName) {
        this.images.add(ProductImage.builder().name(imageName).product(this).build());
    }

    public void addDetail(ProductDetail productDetail) {
        this.details.add(productDetail);
    }

    public void addDetail(Integer id, String name, String value) {
        this.details.add(new ProductDetail(id, name, value, this));
    }

    @Transient
    public String getMainImagePath() {
        if (id == null || mainImage == null) {
            return "/images/image-thumbnail.png";
        }
        return "/product-images/" + this.id + "/" + this.mainImage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean containsImageName(String fileName) {
        return images.stream().anyMatch(productImage -> productImage.getName().equals(fileName));
    }
}
