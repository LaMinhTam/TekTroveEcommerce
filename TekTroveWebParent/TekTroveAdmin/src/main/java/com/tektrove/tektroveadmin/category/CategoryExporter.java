package com.tektrove.tektroveadmin.category;

import com.opencsv.CSVWriter;
import com.tektrove.tektroveadmin.utils.ResponseHeaderUtil;
import com.tektrovecommon.entity.Category;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class CategoryExporter {

    public static void exportCSV(List<Category> categories, HttpServletResponse response) throws IOException {
        ResponseHeaderUtil.setExportReportResponseHeader(response, "text/csv", ".csv", "categories_");

        try (CSVWriter writer = new CSVWriter(response.getWriter())) {
            writer.writeNext(new String[]{"ID", "Name"});

            for (Category category : categories) {
                writer.writeNext(new String[]{
                        String.valueOf(category.getId()),
                        category.getName().replace("--","  ")
                });
            }
        }
    }
}
