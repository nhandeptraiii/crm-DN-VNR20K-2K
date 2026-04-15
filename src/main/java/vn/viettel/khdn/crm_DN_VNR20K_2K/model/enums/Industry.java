package vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums;

import java.text.Normalizer;
import lombok.Getter;

@Getter
public enum Industry {
    REAL_ESTATE("Bất động sản", new String[] { "BĐS", "Nha dat", "Bat dong san" }),
    TOURISM_RESTAURANT("Du lịch, nhà hàng", new String[] { "Khach san", "An uong", "Am thuc", "Du lich" }),
    OTHER_SERVICES("Các dịch vụ khác", new String[] {}),
    IT_SOFTWARE("Công nghệ, phần mềm",
            new String[] { "IT", "Phan mem", "Cong nghe", "CNTT", "Phần mềm", "Công nghệ thông tin" }),
    MEDIA_ADVERTISING("Truyền thông, quảng cáo", new String[] { "Truyen thong", "Quang cao", "Marketing", "Ads" }),
    AGRICULTURE_AQUACULTURE("Nông nghiệp, thủy sản", new String[] { "Nong nghiep", "Thuy san", "Hai san" }),
    COMMERCE_DISTRIBUTION("Thương mại, phân phối",
            new String[] { "Thuong mai", "Phan phoi", "Ban le", "Dich vu", "Thương mại" }),
    SANITATION_SECURITY("Vệ sinh, Bảo vệ", new String[] { "Ve sinh", "Bao ve", "An ninh" }),
    CONSTRUCTION_DESIGN("Xây dựng, thiết kế", new String[] { "Xay dung", "Thiet ke", "Kien truc", "Xây lắp" }),
    MANUFACTURING_PROCESSING_IMPORT_EXPORT("Công nghiệp chế biến, sản xuất, xuất nhập khẩu",
            new String[] { "San xuat", "Xuat nhap khau", "Che bien", "Cong nghiep", "XNK" }),
    ENVIRONMENT_FIRE_PROTECTION("Dịch vụ môi trường, PCCC",
            new String[] { "Moi truong", "PCCC", "Phong chay chua chay" }),
    LEGAL_INSPECTION_TESTING("Dịch vụ Pháp lý - Giám định - Kiểm định",
            new String[] { "Phap ly", "Giam dinh", "Kiem dinh", "Luat" }),
    ACCOUNTING_AUDITING_TAX("Kế toán, kiểm toán, thuế", new String[] { "Ke toan", "Kiem toan", "Thue" }),
    EDUCATION("Giáo dục", new String[] { "Giao duc", "Dao tao", "Truong hoc", "Hoc vien" }),
    LOGISTICS("Logistics", new String[] { "Van tai", "Giao nhan", "Kho bai", "Vận tải" }),
    BANKING_FINANCE_INSURANCE("Ngân hàng - Tài Chính - Bảo hiểm",
            new String[] { "Ngan hang", "Tai chinh", "Bao hiem", "Bank" }),
    HEALTHCARE("Y tế", new String[] { "Y te", "Suc khoe", "Benh vien", "Phong kham", "Nha thuoc" });

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
        if (value == null || value.isBlank()) {
            return OTHER_SERVICES;
        }

        String cleanValue = Normalizer.normalize(value.trim(), Normalizer.Form.NFC);

        for (Industry industry : Industry.values()) {
            if (industry.name().equalsIgnoreCase(cleanValue)) {
                return industry;
            }
            String normalizedDisplay = Normalizer.normalize(industry.getDisplayName(), Normalizer.Form.NFC);
            if (normalizedDisplay.equalsIgnoreCase(cleanValue)) {
                return industry;
            }

            if (industry.getAliases() != null) {
                for (String alias : industry.getAliases()) {
                    String normalizedAlias = Normalizer.normalize(alias, Normalizer.Form.NFC);
                    if (normalizedAlias.equalsIgnoreCase(cleanValue)) {
                        return industry;
                    }
                }
            }
        }

        return OTHER_SERVICES;
    }
}
