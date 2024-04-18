package com.tektrovecommon.entity;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import jakarta.persistence.*;
import lombok.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class User extends IdBaseEntity implements Exportable {
    @Column(length = 128, nullable = false, unique = true)
    private String email;
    private boolean enabled;
    @Column(length = 45, nullable = false)
    private String firstName;
    @Column(length = 45, nullable = false)
    private String lastName;
    @Column(length = 64, nullable = false)
    private String password;
    @Column(length = 64)
    private String photos;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public User(String email, String firstName, String lastName, String password) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    @Transient
    public String getPhotosImagePath() {
        if (id == null || photos == null) return "/images/default-user.png";

        return "/user-photos/" + this.id + "/" + this.photos;
    }

    @Transient
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Transient
    public String getRolesAsString() {
        StringBuilder rolesAsString = new StringBuilder();
        for (Role role : roles) {
            if (!rolesAsString.isEmpty()) {
                rolesAsString.append(", ");
            }
            rolesAsString.append(role.getName());
        }
        return rolesAsString.toString();
    }

    @Override
    public String[] getCsvExportData() {
        return new String[]{
                String.valueOf(this.id),
                this.email,
                String.valueOf(this.enabled),
                this.firstName,
                this.lastName,
                this.getRolesAsString()
        };
    }

    @Override
    public Row getExcelExportRow(Sheet sheet, int rowNum) {
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(this.id);
        row.createCell(1).setCellValue(this.email);
        row.createCell(2).setCellValue(this.enabled);
        row.createCell(3).setCellValue(this.firstName);
        row.createCell(4).setCellValue(this.lastName);
        row.createCell(5).setCellValue(this.getRolesAsString());
        return row;
    }


    @Override
    public PdfPTable getPdfExportTable(PdfPTable table) throws IOException, DocumentException {
        table.addCell(String.valueOf(this.id));
        table.addCell(this.email);
        table.addCell(this.enabled ? "Enabled" : "Disabled");
        table.addCell(this.firstName);
        table.addCell(this.lastName);
        table.addCell(this.getRolesAsString());
        return table;
    }

    public boolean hasRole(String roleName) {
        for (Role role : roles) {
            if (role.getName().equals(roleName)) {
                return true;
            }
        }
        return false;
    }
}
