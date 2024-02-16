package com.tektrove.tektroveadmin.user;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.opencsv.CSVWriter;
import com.tektrove.tektroveadmin.utils.ResponseHeaderUtil;
import com.tektrovecommon.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public class UserExporter {

    public static void exportCSV(List<User> users, HttpServletResponse response) throws IOException {
        ResponseHeaderUtil.setExportReportResponseHeader(response, "text/csv", ".csv", "users_");

        try (CSVWriter writer = new CSVWriter(response.getWriter())) {
            writer.writeNext(new String[]{"ID", "Email", "Enabled", "First Name", "Last Name", "Roles"});

            for (User user : users) {
                writer.writeNext(new String[]{
                        String.valueOf(user.getId()),
                        user.getEmail(),
                        String.valueOf(user.isEnabled()),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getRolesAsString()
                });
            }
        }
    }

    public static void exportExcel(List<User> users, HttpServletResponse response) throws IOException {
        ResponseHeaderUtil.setExportReportResponseHeader(response, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx", "users_");

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Users");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID", "Email", "Enabled", "First Name", "Last Name", "Roles"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (User user : users) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(user.getId());
                row.createCell(1).setCellValue(user.getEmail());
                row.createCell(2).setCellValue(user.isEnabled());
                row.createCell(3).setCellValue(user.getFirstName());
                row.createCell(4).setCellValue(user.getLastName());
                row.createCell(5).setCellValue(user.getRolesAsString());
            }

            workbook.write(out);
            response.getOutputStream().write(out.toByteArray());
        }
    }

    public static void exportPdf(List<User> users, HttpServletResponse response) throws IOException, DocumentException {
        ResponseHeaderUtil.setExportReportResponseHeader(response, "application/pdf", ".pdf", "users");

        Document document = new Document();
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        PdfPTable table = new PdfPTable(6);

        Stream.of("ID", "Email", "Enabled", "First Name", "Last Name", "Roles")
                .forEach(headerTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(1);
                    header.setPhrase(new Phrase(headerTitle));
                    table.addCell(header);
                });

        users.forEach(user -> {
            table.addCell(String.valueOf(user.getId()));
            table.addCell(user.getEmail());
            table.addCell(user.isEnabled() ? "Enabled" : "Disabled");
            table.addCell(user.getFirstName());
            table.addCell(user.getLastName());
            table.addCell(user.getRolesAsString());
        });

        document.add(table);
        document.close();
    }

}
