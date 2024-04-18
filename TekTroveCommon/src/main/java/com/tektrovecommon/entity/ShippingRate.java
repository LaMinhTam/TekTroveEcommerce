package com.tektrovecommon.entity;

import com.tektrovecommon.entity.setting.Country;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "shipping_rates")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShippingRate extends IdBaseEntity {
    private float rate;
    private int days;
    private boolean codSupported;
    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;
    @Column(nullable = false, length = 45)
    private String state;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShippingRate that = (ShippingRate) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
