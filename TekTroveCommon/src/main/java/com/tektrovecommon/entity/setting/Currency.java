package com.tektrovecommon.entity.setting;

import com.tektrovecommon.entity.IdBaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "currencies")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Currency extends IdBaseEntity {
    @Column(nullable = false, length = 64)
    private String name;
    @Column(nullable = false, length = 3)
    private String symbol;
    @Column(nullable = false, length = 3)
    private String code;

    @Override
    public String toString() {
        return name + " - " + code + " - " + symbol;
    }
}
