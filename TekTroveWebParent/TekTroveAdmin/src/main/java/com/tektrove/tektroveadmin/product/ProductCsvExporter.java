package com.tektrove.tektroveadmin.product;

import com.opencsv.CSVWriter;
import com.tektrove.tektroveadmin.utils.ResponseHeaderUtil;
import com.tektrovecommon.entity.Category;
import com.tektrovecommon.entity.product.Product;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class ProductCsvExporter {
    public void export(List<Product> products, HttpServletResponse response) throws IOException {
        ResponseHeaderUtil.setExportReportResponseHeader(response, "text/csv", ".csv", "categories_");

        try (CSVWriter writer = new CSVWriter(response.getWriter())) {
            writer.writeNext(new String[]{"ID", "Name"});

            for (Product product : products) {
                writer.writeNext(new String[]{
                        String.valueOf(product.getId()),
                        product.getName(),
                        product.getAlias(),
                        product.getShortDescription(),
                        product.getFullDescription(),
                        product.getCategory().getName(),
                        product.getBrand().getName(),
                        String.valueOf(product.getCost()),
                        String.valueOf(product.getPrice()),
                        String.valueOf(product.getDiscountPercent()),
                        String.valueOf(product.getLength()),
                        String.valueOf(product.getWidth()),
                        String.valueOf(product.getHeight()),
                        String.valueOf(product.getWeight()),
                });
            }
        }
    }
}
