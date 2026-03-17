package vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums;

import lombok.Getter;

@Getter
public enum Industry {
    REAL_ESTATE("Bất động sản"),
    TOURISM_RESTAURANT("Du lịch, nhà hàng"),
    OTHER_SERVICES("Các dịch vụ khác"),
    IT_SOFTWARE("Công nghệ, phần mềm"),
    MEDIA_ADVERTISING("Truyền thông, quảng cáo"),
    AGRICULTURE_AQUACULTURE("Nông nghiệp, thủy sản"),
    COMMERCE_DISTRIBUTION("Thương mại, phân phối"),
    SANITATION_SECURITY("Vệ sinh, Bảo vệ"),
    CONSTRUCTION_DESIGN("Xây dựng, thiết kế"),
    MANUFACTURING_PROCESSING_IMPORT_EXPORT("Công nghiệp chế biến, sản xuất, xuất nhập khẩu"),
    ENVIRONMENT_FIRE_PROTECTION("Dịch vụ môi trường, PCCC"),
    LEGAL_INSPECTION_TESTING("Dịch vụ Pháp lý - Giám định - Kiểm định"),
    ACCOUNTING_AUDITING_TAX("Kế toán, kiểm toán, thuế"),
    EDUCATION("Giáo dục"),
    LOGISTICS("Logistics"),
    BANKING_FINANCE_INSURANCE("Ngân hàng - Tài Chính - Bảo hiểm"),
    HEALTHCARE("Y tế");

    private final String displayName;

    Industry(String displayName) {
        this.displayName = displayName;
    }
}
