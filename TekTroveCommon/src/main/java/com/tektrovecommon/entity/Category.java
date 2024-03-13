package com.tektrovecommon.entity;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import jakarta.persistence.*;
import lombok.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "categories")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = {"parent", "children"})
public class Category implements Exportable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 128, nullable = false, unique = true)
    private String name;
    @Column(length = 64, nullable = false, unique = true)
    private String alias;
    @Column(length = 128, nullable = false)
    private String image;
    private boolean enabled;
    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private Set<Category> children = new HashSet<>();
    @Column(name = "all_parent_ids", length = 256)
    private String allParentIDs;
    public static Category copyCategory(Category category) {
        Category copyCategory = new Category();
        copyCategory.setId(category.getId());
        copyCategory.setName(category.getName());
        copyCategory.setImage(category.getImage());
        copyCategory.setAlias(category.getAlias());
        copyCategory.setEnabled(category.isEnabled());
        copyCategory.setParent(category.getParent());
        copyCategory.setChildren(category.getChildren());
        return copyCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category category)) return false;
        return Objects.equals(getId(), category.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Transient
    private boolean hasChildren;

    public boolean isHasChildren() {
        return !children.isEmpty();
    }

    @Transient
    public String getImagePath() {
        if (this.id == null) return "/images/image-thumbnail.png";

        return "/categories-images/" + this.id + "/" + this.image;
    }

    @Override
    public String[] getCsvExportData() {
        return new String[]{
                String.valueOf(this.id),
                this.name.replace("--", "  ")
        };
    }

    @Override
    public Row getExcelExportRow(Sheet sheet, int rowNum) {
        return null;
    }

    @Override
    public PdfPTable getPdfExportTable(PdfPTable table) throws IOException, DocumentException {
        return null;
    }
}
