package com.tektrovecommon.entity.setting;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "currencies")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 64)
    private String name;
    @Column(nullable = false, length = 3)
    private String symbol;
    @Column(nullable = false, length = 3)
    private String code;

    public Currency(String name, String symbol, String code) {
        this.name = name;
        this.symbol = symbol;
        this.code = code;
    }

    @Override
    public String toString() {
        return name + " - " + code + " - " + symbol;
    }
}
