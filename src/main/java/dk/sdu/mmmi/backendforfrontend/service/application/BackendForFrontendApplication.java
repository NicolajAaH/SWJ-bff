package dk.sdu.mmmi.backendforfrontend.service.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages= "dk.sdu.mmmi.backendforfrontend")
public class BackendForFrontendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendForFrontendApplication.class, args);
	}

}
