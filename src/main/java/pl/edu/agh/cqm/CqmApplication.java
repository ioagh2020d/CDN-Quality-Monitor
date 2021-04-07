package pl.edu.agh.cqm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CqmApplication {

	public static void main(String[] args) {
		SpringApplication.run(CqmApplication.class, args);
	}

}
