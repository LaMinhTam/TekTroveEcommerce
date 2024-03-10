package com.tektrovecommon.entity;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.IOException;

public interface Exportable {
    String[] getCsvExportData();

    public Row getExcelExportRow(Sheet sheet, int rowNum);

    PdfPTable getPdfExportTable(PdfPTable table) throws IOException, DocumentException;
}
