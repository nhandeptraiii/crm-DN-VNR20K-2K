package vn.viettel.khdn.crm_DN_VNR20K_2K.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import vn.viettel.khdn.crm_DN_VNR20K_2K.model.Appointment;
import vn.viettel.khdn.crm_DN_VNR20K_2K.model.enums.AppointmentStatus;
import vn.viettel.khdn.crm_DN_VNR20K_2K.repository.AppointmentRepository;

@Component
public class SchedulerService {

    private final AppointmentRepository appointmentRepository;
    private final EmailService emailService;

    public SchedulerService(AppointmentRepository appointmentRepository, EmailService emailService) {
        this.appointmentRepository = appointmentRepository;
        this.emailService = emailService;
    }

    /**
     * Cron job chạy mỗi 15 phút.
     * Kiểm tra và gửi email nhắc lịch hẹn trước 24h và trước 1h.
     */
    @Scheduled(fixedDelay = 15 * 60 * 1000) // 15 phút = 900.000 ms
    public void checkAndSendReminders() {
        sendReminder24h();
        sendReminder1h();
    }

    /**
     * Nhắc trước 24h:
     * Tìm các lịch hẹn có scheduledTime nằm trong khoảng (now+23h, now+25h]
     * và chưa gửi mail nhắc 24h.
     */
    private void sendReminder24h() {
        Instant now = Instant.now();
        Instant from = now.plus(23, ChronoUnit.HOURS);
        Instant to = now.plus(25, ChronoUnit.HOURS);

        List<Appointment> appointments = appointmentRepository.findRemindable24h(from, to);

        for (Appointment appointment : appointments) {
            try {
                String email = appointment.getConsultant().getEmail();
                emailService.sendAppointmentReminder(email, appointment, 24);

                // Đánh dấu đã gửi
                appointment.setReminder24hSent(true);
                appointment.setStatus(AppointmentStatus.REMINDED);
                appointmentRepository.save(appointment);

                System.out.println("[Scheduler] Đã gửi nhắc 24h cho lịch hẹn ID="
                        + appointment.getId() + " đến " + email);
            } catch (Exception e) {
                System.err.println("[Scheduler] Lỗi xử lý nhắc 24h cho lịch hẹn ID="
                        + appointment.getId() + ": " + e.getMessage());
            }
        }
    }

    /**
     * Nhắc trước 1h:
     * Tìm các lịch hẹn có scheduledTime nằm trong khoảng (now+55m, now+65m]
     * và chưa gửi mail nhắc 1h.
     */
    private void sendReminder1h() {
        Instant now = Instant.now();
        Instant from = now.plus(55, ChronoUnit.MINUTES);
        Instant to = now.plus(65, ChronoUnit.MINUTES);

        List<Appointment> appointments = appointmentRepository.findRemindable1h(from, to);

        for (Appointment appointment : appointments) {
            try {
                String email = appointment.getConsultant().getEmail();
                emailService.sendAppointmentReminder(email, appointment, 1);

                // Đánh dấu đã gửi
                appointment.setReminder1hSent(true);
                appointment.setStatus(AppointmentStatus.REMINDED);
                appointmentRepository.save(appointment);

                System.out.println("[Scheduler] Đã gửi nhắc 1h cho lịch hẹn ID="
                        + appointment.getId() + " đến " + email);
            } catch (Exception e) {
                System.err.println("[Scheduler] Lỗi xử lý nhắc 1h cho lịch hẹn ID="
                        + appointment.getId() + ": " + e.getMessage());
            }
        }
    }
}
