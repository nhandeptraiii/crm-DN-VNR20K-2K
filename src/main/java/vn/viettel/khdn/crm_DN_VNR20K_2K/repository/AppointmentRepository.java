package vn.viettel.khdn.crm_DN_VNR20K_2K.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.viettel.khdn.crm_DN_VNR20K_2K.model.Appointment;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.AppointmentStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    /**
     * Tìm lịch hẹn cần gửi email nhắc trước 24h.
     * Điều kiện:
     *   - status không phải CONFIRMED / CANCELLED
     *   - reminder_24h_sent = false
     *   - scheduledTime nằm trong khoảng (from, to) — tức là (now+23h, now+25h)
     */
    @Query("SELECT a FROM Appointment a "
            + "JOIN FETCH a.consultant "
            + "JOIN FETCH a.enterprise "
            + "LEFT JOIN FETCH a.contact "
            + "WHERE a.status NOT IN ('CONFIRMED', 'REJECTED') "
            + "AND a.reminder24hSent = false "
            + "AND a.scheduledTime > :from "
            + "AND a.scheduledTime <= :to")
    List<Appointment> findRemindable24h(@Param("from") Instant from, @Param("to") Instant to);

    /**
     * Tìm lịch hẹn cần gửi email nhắc trước 1h.
     */
    @Query("SELECT a FROM Appointment a "
            + "JOIN FETCH a.consultant "
            + "JOIN FETCH a.enterprise "
            + "LEFT JOIN FETCH a.contact "
            + "WHERE a.status NOT IN ('CONFIRMED', 'REJECTED') "
            + "AND a.reminder1hSent = false "
            + "AND a.scheduledTime > :from "
            + "AND a.scheduledTime <= :to")
    List<Appointment> findRemindable1h(@Param("from") Instant from, @Param("to") Instant to);

    /**
     * Tìm kiếm lịch hẹn có lọc + phân trang.
     */
    @Query("SELECT a FROM Appointment a WHERE "
            + "(:enterpriseId IS NULL OR a.enterprise.id = :enterpriseId) "
            + "AND (:consultantId IS NULL OR a.consultant.id = :consultantId) "
            + "AND (:status IS NULL OR a.status = :status)")
    Page<Appointment> searchAppointments(
            @Param("enterpriseId") Long enterpriseId,
            @Param("consultantId") Long consultantId,
            @Param("status") AppointmentStatus status,
            Pageable pageable);
}
