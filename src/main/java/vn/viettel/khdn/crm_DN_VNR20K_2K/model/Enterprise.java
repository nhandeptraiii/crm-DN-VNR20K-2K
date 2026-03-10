package vn.viettel.khdn.crm_DN_VNR20K_2K.model;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.EnterpriseStatus;

@Entity
@Table(name = "enterprises")
@Getter
@Setter
public class Enterprise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "enterprise_code", unique = true, length = 30)
    private String enterpriseCode;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "tax_code", unique = true, length = 20)
    private String taxCode;

    @Column(length = 100)
    private String industry;

    @Column(name = "employee_count")
    private Integer employeeCount;

    @Column(length = 500)
    private String address;

    @Column(length = 255)
    private String website;

    @Column(name = "established_date")
    private LocalDate establishedDate;

    @Column(length = 30)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private EnterpriseStatus status;

    @Column(length = 1000)
    private String note;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        if (this.status == null) {
            this.status = EnterpriseStatus.PROSPECT;
        }
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedAt = Instant.now();
    }
}
