package vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums;

import lombok.Getter;

@Getter
public enum Industry {
    REAL_ESTATE("Bất động sản", new String[]{"BĐS", "Nha dat", "Bat dong san"}),
    TOURISM_RESTAURANT("Du lịch, nhà hàng", new String[]{"Khach san", "An uong", "Am thuc", "Du lich"}),
    OTHER_SERVICES("Các dịch vụ khác", new String[]{}),
    IT_SOFTWARE("Công nghệ, phần mềm", new String[]{"IT", "Phan mem", "Cong nghe", "CNTT", "Phần mềm", "Công nghệ thông tin"}),
    MEDIA_ADVERTISING("Truyền thông, quảng cáo", new String[]{"Truyen thong", "Quang cao", "Marketing", "Ads"}),
    AGRICULTURE_AQUACULTURE("Nông nghiệp, thủy sản", new String[]{"Nong nghiep", "Thuy san", "Hai san"}),
    COMMERCE_DISTRIBUTION("Thương mại, phân phối", new String[]{"Thuong mai", "Phan phoi", "Ban le", "Dich vu", "Thương mại"}),
    SANITATION_SECURITY("Vệ sinh, Bảo vệ", new String[]{"Ve sinh", "Bao ve", "An ninh"}),
    CONSTRUCTION_DESIGN("Xây dựng, thiết kế", new String[]{"Xay dung", "Thiet ke", "Kien truc", "Xây lắp"}),
    MANUFACTURING_PROCESSING_IMPORT_EXPORT("Công nghiệp chế biến, sản xuất, xuất nhập khẩu", new String[]{"San xuat", "Xuat nhap khau", "Che bien", "Cong nghiep", "XNK"}),
    ENVIRONMENT_FIRE_PROTECTION("Dịch vụ môi trường, PCCC", new String[]{"Moi truong", "PCCC", "Phong chay chua chay"}),
    LEGAL_INSPECTION_TESTING("Dịch vụ Pháp lý - Giám định - Kiểm định", new String[]{"Phap ly", "Giam dinh", "Kiem dinh", "Luat"}),
    ACCOUNTING_AUDITING_TAX("Kế toán, kiểm toán, thuế", new String[]{"Ke toan", "Kiem toan", "Thue", "Tai chinh"}),
    EDUCATION("Giáo dục", new String[]{"Giao duc", "Dao tao", "Truong hoc", "Hoc vien"}),
    LOGISTICS("Logistics", new String[]{"Van tai", "Giao nhan", "Kho bai", "Vận tải"}),
    BANKING_FINANCE_INSURANCE("Ngân hàng - Tài Chính - Bảo hiểm", new String[]{"Ngan hang", "Tai chinh", "Bao hiem", "Bank"}),
    HEALTHCARE("Y tế", new String[]{"Y te", "Suc khoe", "Benh vien", "Phong kham", "Nha thuoc"});

    private final String displayName;
    private final String[] aliases;

    Industry(String displayName, String[] aliases) {
        this.displayName = displayName;
        this.aliases = aliases;
    }

    public String[] getAliases() {
        return aliases;
    }

    public static Industry fromValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return OTHER_SERVICES;
        }

        String cleanValue = value.trim().toLowerCase();

        for (Industry i : Industry.values()) {
            // 1. So khớp chính xác theo Enum name (ví dụ: IT_SOFTWARE)
            if (i.name().toLowerCase().equals(cleanValue)) {
                return i;
            }

            // 2. So khớp theo Tên hiển thị (ví dụ: Công nghệ, phần mềm)
            if (i.getDisplayName().toLowerCase().equals(cleanValue)) {
                return i;
            }

            // 3. So khớp theo danh sách Bí danh (Alias)
            if (i.getAliases() != null) {
                for (String alias : i.getAliases()) {
                    if (alias.toLowerCase().equals(cleanValue)) {
                        return i;
                    }
                }
            }
        }

        // Không tìm thấy gì thì gom vào dịch vụ khác
        return OTHER_SERVICES;
    }
}
