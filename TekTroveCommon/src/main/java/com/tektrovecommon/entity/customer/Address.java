package com.tektrovecommon.entity.customer;

import com.tektrovecommon.entity.setting.Country;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 64)
    private String firstName;
    @Column(nullable = false, length = 64)
    private String lastName;
    @Column(nullable = false, length = 15)
    private String phoneNumber;
    @Column(nullable = false, length = 64)
    private String addressLine1;
    @Column(length = 64)
    private String addressLine2;
    @Column(nullable = false, length = 45)
    private String city;
    @Column(nullable = false, length = 45)
    private String state;
    @Column(nullable = false, length = 10)
    private String postalCode;
    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    private boolean isDefaultAddress;

    @Override
    public String toString() {
        String address = firstName;

        if (lastName != null && !lastName.isEmpty()) address += " " + lastName;

        if (!addressLine1.isEmpty()) address += ", " + addressLine1;

        if (addressLine2 != null && !addressLine2.isEmpty()) address += ", " + addressLine2;

        if (!city.isEmpty()) address += ", " + city;

        if (state != null && !state.isEmpty()) address += ", " + state;

        address += ", " + country.getName();

        if (!postalCode.isEmpty()) address += ". Postal Code: " + postalCode;
        if (!phoneNumber.isEmpty()) address += ". Phone Number: " + phoneNumber;

        return address;
    }
}
