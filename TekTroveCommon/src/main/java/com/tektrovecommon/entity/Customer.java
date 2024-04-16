package com.tektrovecommon.entity;

import com.tektrovecommon.entity.setting.Country;
import jakarta.annotation.Resource;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "customers")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 45)
    private String email;
    @Column(nullable = false, length = 64)
    private String password;
    @Column(nullable = false, length = 45)
    private String firstName;
    @Column(nullable = false, length = 45)
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
    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;
    @Column(nullable = false, length = 10)
    private String postalCode;
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
    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
