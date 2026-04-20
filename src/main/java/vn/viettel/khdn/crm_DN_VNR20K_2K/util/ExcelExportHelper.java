package vn.viettel.khdn.crm_DN_VNR20K_2K.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ResEnterpriseDTO;

public class ExcelExportHelper {

    public static String[] HEADERS = {
            "STT", "Tên doanh nghiệp (*)", "Mã số thuế (*)", "Cơ quan thuế", "Lĩnh vực", "Số nhân viên",
            "Địa chỉ", "Website", "Ngày thành lập", "Điện thoại", "Loại hình", 
            "DT < 500tr (X)", "DT 500tr - 1 tỷ (X)", "DT > 1 tỷ (X)", 
            "Vùng", "Tên xã", "Ghi chú",
            "Họ tên NĐD", "Email NĐD", "SĐT NĐD", "Chức vụ NĐD"
    };

    public static String SHEET_NAME = "Doanh Nghiep";

    public static ByteArrayInputStream enterprisesToExcel(List<ResEnterpriseDTO> enterprises) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet(SHEET_NAME);

            String[] headersArr = HEADERS;

            // Style for Header
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font font = workbook.createFont();
            font.setBold(true);
            font.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(font);

            // Row for Header
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < headersArr.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(headersArr[col]);
                cell.setCellStyle(headerStyle);
            }

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            // Row for Data
            int rowIdx = 1;
            for (ResEnterpriseDTO enterprise : enterprises) {
                Row row = sheet.createRow(rowIdx);

                row.createCell(0).setCellValue(rowIdx); // STT
                row.createCell(1).setCellValue(enterprise.getName());
                row.createCell(2).setCellValue(enterprise.getTaxCode());

                row.createCell(3).setCellValue(enterprise.getTaxAuthority() != null ? enterprise.getTaxAuthority() : "");
                row.createCell(4).setCellValue(enterprise.getIndustry() != null ? enterprise.getIndustry().getDisplayName() : "");
                row.createCell(5).setCellValue(enterprise.getEmployeeCount() != null ? enterprise.getEmployeeCount().toString() : "");
                row.createCell(6).setCellValue(enterprise.getAddress() != null ? enterprise.getAddress() : "");
                row.createCell(7).setCellValue(enterprise.getWebsite() != null ? enterprise.getWebsite() : "");
                
                if (enterprise.getEstablishedDate() != null) {
                    row.createCell(8).setCellValue(enterprise.getEstablishedDate().format(dtf));
                } else {
                    row.createCell(8).setCellValue("");
                }

                row.createCell(9).setCellValue(enterprise.getPhone() != null ? enterprise.getPhone() : "");
                row.createCell(10).setCellValue(enterprise.getType() != null ? enterprise.getType().name() : "");
                
                // Thuế HKD: Điền dấu "x" (hoặc "X") nếu khớp class Enum
                row.createCell(11).setCellValue(enterprise.getRevenueRange() == vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.RevenueRange.UNDER_500M ? "X" : "");
                row.createCell(12).setCellValue(enterprise.getRevenueRange() == vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.RevenueRange.FROM_500M_TO_1B ? "X" : "");
                row.createCell(13).setCellValue(enterprise.getRevenueRange() == vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.RevenueRange.OVER_1B ? "X" : "");

                row.createCell(14).setCellValue(enterprise.getRegion() != null ? enterprise.getRegion().name() : "");
                row.createCell(15).setCellValue(enterprise.getCommuneName() != null ? enterprise.getCommuneName() : "");
                row.createCell(16).setCellValue(enterprise.getNote() != null ? enterprise.getNote() : "");
                row.createCell(17).setCellValue(enterprise.getContactFullName() != null ? enterprise.getContactFullName() : "");
                row.createCell(18).setCellValue(enterprise.getContactEmail() != null ? enterprise.getContactEmail() : "");
                row.createCell(19).setCellValue(enterprise.getContactPhone() != null ? enterprise.getContactPhone() : "");
                row.createCell(20).setCellValue(enterprise.getContactPosition() != null ? enterprise.getContactPosition() : "");

                rowIdx++;
            }

            // Auto size columns
            for (int col = 0; col < headersArr.length; col++) {
                sheet.autoSizeColumn(col);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public static ByteArrayInputStream createTemplateExcel() throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet(SHEET_NAME);

            String[] headersArr = HEADERS;

            // Style for Header
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            // Row for Header
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < headersArr.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(headersArr[col]);
                cell.setCellStyle(headerStyle);
            }

            // Thêm 1 dòng ví dụ
            Row exampleRow = sheet.createRow(1);
            exampleRow.createCell(0).setCellValue(1);
            exampleRow.createCell(1).setCellValue("Công ty TNHH Mẫu");
            exampleRow.createCell(2).setCellValue("0123456789");
            exampleRow.createCell(3).setCellValue("Cục Thuế TP Hà Nội");
            exampleRow.createCell(4).setCellValue("Công nghệ, phần mềm"); // Industry
            exampleRow.createCell(5).setCellValue(50);
            exampleRow.createCell(6).setCellValue("Hà Nội");
            exampleRow.createCell(7).setCellValue("https://example.com");
            exampleRow.createCell(8).setCellValue("01-01-2020");
            exampleRow.createCell(9).setCellValue("0901234567");
            
            exampleRow.createCell(10).setCellValue("HKD"); // Type
            exampleRow.createCell(11).setCellValue(""); // DT < 500tr
            exampleRow.createCell(12).setCellValue("X"); // DT 500tr - 1 tỷ
            exampleRow.createCell(13).setCellValue(""); // DT > 1 tỷ

            exampleRow.createCell(14).setCellValue("CTO"); // Region
            exampleRow.createCell(15).setCellValue("Phường An Thới Đông"); // Tên xã
            exampleRow.createCell(16).setCellValue("Đây là dữ liệu mẫu, vui lòng xóa dòng này trước khi import");
            exampleRow.createCell(17).setCellValue("Nguyễn Văn Mẫu");
            exampleRow.createCell(18).setCellValue("mau@example.com");
            exampleRow.createCell(19).setCellValue("0901234567");
            exampleRow.createCell(20).setCellValue("Giám đốc");

            // Auto size columns
            for (int col = 0; col < headersArr.length; col++) {
                sheet.autoSizeColumn(col);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
