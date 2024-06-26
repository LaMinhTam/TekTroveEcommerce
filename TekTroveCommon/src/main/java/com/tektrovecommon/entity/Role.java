package com.tektrovecommon.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "roles")
public class Role extends IdBaseEntity{
    @Column(length = 40, nullable = false, unique = true)
    private String name;
    @Column(length = 150, nullable = false)
    private String description;

    public Role(Integer id) {
        this.id = id;
    }

    public Role(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role role)) return false;
        return Objects.equals(getId(), role.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return name;
    }
}
