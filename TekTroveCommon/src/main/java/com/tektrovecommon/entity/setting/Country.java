package com.tektrovecommon.entity.setting;

import com.tektrovecommon.entity.IdBaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "countries")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Country extends IdBaseEntity {
    @Column(nullable = false, length = 45)
    private String name;
    @Column(nullable = false, length = 5)
    private String code;
    @OneToMany(mappedBy = "country")
    private Set<State> states = new HashSet<>();

}
