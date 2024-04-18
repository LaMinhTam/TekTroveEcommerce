package com.tektrovecommon.entity.customer;

import com.tektrovecommon.entity.AbstractAddress;
import com.tektrovecommon.entity.AbstractAddressWithCountry;
import com.tektrovecommon.entity.AuthenticationType;
import com.tektrovecommon.entity.IdBaseEntity;
import com.tektrovecommon.entity.setting.Country;
import jakarta.annotation.Resource;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "customers")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer extends AbstractAddressWithCountry {
    @Column(nullable = false, length = 45)
    private String email;
    @Column(nullable = false, length = 64)
    private String password;
    @Column(nullable = false)
    private Date createdTime;
    private boolean enabled;
    @Column(length = 64)
    private String activationCode;
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private AuthenticationType authenticationType;
    @Column(length = 30)
    private String resetPasswordToken;

    public Customer(int id) {
        this.id = id;
    }

    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
