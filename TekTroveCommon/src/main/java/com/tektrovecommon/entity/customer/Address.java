package com.tektrovecommon.entity.customer;

import com.tektrovecommon.entity.AbstractAddress;
import com.tektrovecommon.entity.AbstractAddressWithCountry;
import com.tektrovecommon.entity.IdBaseEntity;
import com.tektrovecommon.entity.setting.Country;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address extends AbstractAddressWithCountry {
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    private boolean isDefaultAddress;
}
