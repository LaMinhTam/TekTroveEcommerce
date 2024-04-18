package com.tektrovecommon.entity.order;

import com.tektrovecommon.entity.AbstractAddress;
import com.tektrovecommon.entity.IdBaseEntity;
import com.tektrovecommon.entity.customer.Customer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order extends AbstractAddress {
    @Column(nullable = false, length = 45)
    private String country;
    private Date orderTime;
    private float shippingCost;
    private float productCost;
    private float subtotal;
    private float tax;
    private float total;
    private int deliverDays;
    private Date deliverDate;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrderDetail> orderDetails = new HashSet<>();

    public void copyAddressFromCustomer() {
        this.firstName = customer.getFirstName();
        this.lastName = customer.getLastName();
        this.phoneNumber = customer.getPhoneNumber();
        this.addressLine1 = customer.getAddressLine1();
        this.addressLine2 = customer.getAddressLine2();
        this.city = customer.getCity();
        this.state = customer.getState();
        this.postalCode = customer.getPostalCode();
        this.country = customer.getCountry().getName();
    }

    @Transient
    public String getDestination() {
        String destination =  city + ", ";
        if (state != null && !state.isEmpty()) destination += state + ", ";
        destination += country;

        return destination;
    }
}
