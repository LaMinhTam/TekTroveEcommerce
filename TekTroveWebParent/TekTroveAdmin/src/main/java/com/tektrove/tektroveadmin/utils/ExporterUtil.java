package com.tektrove.tektroveadmin.utils;

import com.opencsv.CSVWriter;
import com.tektrovecommon.entity.Exportable;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class ExporterUtil {
    public static <T extends Exportable> void exportToCsv(List<T> data, String[] headers, HttpServletResponse response) throws IOException {
        ResponseHeaderUtil.setExportReportResponseHeader(response, "text/csv", ".csv", "data_");

        try (CSVWriter writer = new CSVWriter(response.getWriter())) {
            writer.writeNext(headers);

            for (T item : data) {
                writer.writeNext(item.getExportData());
            }
        }
    }
}
