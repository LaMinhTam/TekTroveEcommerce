package com.tektrovecommon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
@Data
public abstract class AbstractAddress extends IdBaseEntity {
    @Column(nullable = false, length = 64)
    protected String firstName;
    @Column(nullable = false, length = 64)
    protected String lastName;
    @Column(nullable = false, length = 15)
    protected String phoneNumber;
    @Column(nullable = false, length = 64)
    protected String addressLine1;
    @Column(length = 64)
    protected String addressLine2;
    @Column(nullable = false, length = 45)
    protected String city;
    @Column(nullable = false, length = 45)
    protected String state;
    @Column(nullable = false, length = 10)
    protected String postalCode;
}
