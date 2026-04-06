package vn.viettel.khdn.crm_DN_VNR20K_2K.util;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import vn.viettel.khdn.crm_DN_VNR20K_2K.model.dto.ReqEnterpriseCreateDTO;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.Industry;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.RegionEnum;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.EnterpriseTypeEnum;

public class ExcelUtils {
    public static String EXCEL_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public static boolean hasExcelFormat(MultipartFile file) {
        return EXCEL_TYPE.equals(file.getContentType());
    }

    public static LocalDate parseFlexibleDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        
        String[] patterns = {
            "dd-MM-yyyy", "dd/MM/yyyy", "dd.MM.yyyy",
            "yyyy-MM-dd", "yyyy/MM/dd",
            "d/M/yyyy", "d-M-yyyy",
            "dd-MM-yy",
            "MM-dd-yyyy", "MM/dd/yyyy", 
            "M-d-yyyy", "M/d/yyyy"
        };

        for (String pattern : patterns) {
            try {
                return LocalDate.parse(dateStr.trim(), DateTimeFormatter.ofPattern(pattern));
            } catch (DateTimeParseException ignored) {
            }
        }
        return null;
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    // Trả về định dạng chuẩn if it's a date in excel
                    return cell.getLocalDateTimeCellValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                }
                // Convert double to String without scientific notation or decimal .0
                double val = cell.getNumericCellValue();
                if (val == (long) val) {
                    return String.valueOf((long) val);
                } else {
                    return String.valueOf(val);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }

    private static Integer getCellValueAsInteger(Cell cell) {
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Integer.parseInt(cell.getStringCellValue().trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    // Trả về danh sách DTO, KHÔNG bao gồm báo lỗi (Xử lý lỗi ở Service)
    public static List<ReqEnterpriseCreateDTO> excelToEnterprises(InputStream is) {
        List<ReqEnterpriseCreateDTO> enterprises = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            // Bỏ qua dòng Header (index 0)
            int rowIndex = 0;
            for (Row row : sheet) {
                if (rowIndex == 0) {
                    rowIndex++;
                    continue;
                }

                ReqEnterpriseCreateDTO dto = new ReqEnterpriseCreateDTO();
                
                // Cột 1: Tên doanh nghiệp
                dto.setName(getCellValueAsString(row.getCell(1)));
                
                // Cột 2: Mã số thuế
                dto.setTaxCode(getCellValueAsString(row.getCell(2)));
                
                // Cột 3: Lĩnh vực
                String industryStr = getCellValueAsString(row.getCell(3));
                dto.setIndustry(Industry.fromValue(industryStr));
                
                // Cột 4: Số nhân viên
                dto.setEmployeeCount(getCellValueAsInteger(row.getCell(4)));
                
                // Cột 5: Địa chỉ
                dto.setAddress(getCellValueAsString(row.getCell(5)));
                
                // Cột 6: Website
                dto.setWebsite(getCellValueAsString(row.getCell(6)));
                
                // Cột 7: Ngày thành lập
                String dateStr = getCellValueAsString(row.getCell(7));
                dto.setEstablishedDate(parseFlexibleDate(dateStr));

                // Cột 8: Điện thoại
                dto.setPhone(getCellValueAsString(row.getCell(8)));
                
                // Cột 9: Ghi chú
                dto.setNote(getCellValueAsString(row.getCell(9)));

                // Cột 10: Họ tên NĐD
                dto.setContactFullName(getCellValueAsString(row.getCell(10)));
                
                // Cột 11: Email NĐD
                dto.setContactEmail(getCellValueAsString(row.getCell(11)));
                
                // Cột 12: SĐT NĐD
                dto.setContactPhone(getCellValueAsString(row.getCell(12)));
                
                // Cột 13: Chức vụ NĐD
                dto.setContactPosition(getCellValueAsString(row.getCell(13)));

                // Cột 14: Vùng
                String regionStr = getCellValueAsString(row.getCell(14));
                if (regionStr != null && !regionStr.isBlank()) {
                    try {
                        dto.setRegion(RegionEnum.valueOf(regionStr.trim().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                    }
                }

                // Cột 15: Loại hình
                String typeStr = getCellValueAsString(row.getCell(15));
                if (typeStr != null && !typeStr.isBlank()) {
                    try {
                        dto.setType(EnterpriseTypeEnum.valueOf(typeStr.trim().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                    }
                }

                enterprises.add(dto);
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi đọc file Excel: " + e.getMessage());
        }

        return enterprises;
    }
}
