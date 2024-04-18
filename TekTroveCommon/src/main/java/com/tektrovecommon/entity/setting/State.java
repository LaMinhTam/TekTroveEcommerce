package com.tektrovecommon.entity.setting;

import com.tektrovecommon.entity.IdBaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "states")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class State extends IdBaseEntity {
    @Column(nullable = false, length = 45)
    private String name;
    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;
}
