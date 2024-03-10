package com.tektrove.tektroveadmin.utils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.opencsv.CSVWriter;
import com.tektrovecommon.entity.Exportable;
import com.tektrovecommon.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class ExporterUtil {
    public static void setExportReportResponseHeader(HttpServletResponse response, String contentType,
                                                     String extension, String prefix) {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timestamp = dateFormatter.format(new Date());
        String fileName = prefix + "_" + timestamp + extension;

        response.setContentType(contentType);

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=\"" + fileName + "\"";
        response.setHeader(headerKey, headerValue);
    }

    public static <T extends Exportable> void exportToCsv(List<T> data, String[] headers, HttpServletResponse response) throws IOException {
        setExportReportResponseHeader(response, "text/csv", ".csv", "data_");

        try (CSVWriter writer = new CSVWriter(response.getWriter())) {
            writer.writeNext(headers);

            for (T item : data) {
                writer.writeNext(item.getCsvExportData());
            }
        }
    }

    public static <T extends Exportable> void exportToExcel(List<T> data, String[] headers, HttpServletResponse response) throws IOException {
        setExportReportResponseHeader(response, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx", "data_");

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Data");

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (T item : data) {
                Row row = item.getExcelExportRow(sheet, rowNum++);
            }

            workbook.write(out);
            response.getOutputStream().write(out.toByteArray());
        }
    }

    public static void exportToPdf(List<User> users, String[] headers, HttpServletResponse response) throws IOException, DocumentException {
        ExporterUtil.setExportReportResponseHeader(response, "application/pdf", ".pdf", "users");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        PdfPTable table = new PdfPTable(6);

        PdfPTable finalTable = table;
        Stream.of(headers)
                .forEach(headerTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(1);
                    header.setPhrase(new Phrase(headerTitle));
                    finalTable.addCell(header);
                });

        for (User user : users) {
            table = user.getPdfExportTable(table);
        }

        document.add(table);
        document.close();
    }
}
