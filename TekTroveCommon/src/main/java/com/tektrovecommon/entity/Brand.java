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
import java.util.stream.Collectors;

@Entity
@Table(name = "brands")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Brand  implements Exportable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 45, unique = true)
    private String name;
    @Column(nullable = false, length = 128)
    private String logo;
    @ManyToMany
    @JoinTable(name = "brands_categories",
            joinColumns = @JoinColumn(name = "brand_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private Set<Category> categories = new HashSet<>();

    public Brand(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Transient
    public String getLogoPath() {
        if (this.id == null) return "/images/image-thumbnail.png";

        return "/brand-logos/" + this.id + "/" + this.logo;
    }

    @Override
    public String toString() {
        return "Brand{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", logo='" + logo + '\'' +
                '}';
    }

    @Override
    public String[] getCsvExportData() {
        String categoryNames = this.categories.stream()
                .map(Category::getName)
                .collect(Collectors.joining(", "));
        return new String[]{
                String.valueOf(this.id),
                this.name,
                categoryNames
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
