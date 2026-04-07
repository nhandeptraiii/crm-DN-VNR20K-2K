package vn.viettel.khdn.crm_DN_VNR20K_2K;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class CrmDnVnr20K2KApplication {

	@PostConstruct
	public void init() {
		// Ép JVM chạy trên múi giờ Việt Nam
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
	}

	public static void main(String[] args) {
		SpringApplication.run(CrmDnVnr20K2KApplication.class, args);
	}

}
