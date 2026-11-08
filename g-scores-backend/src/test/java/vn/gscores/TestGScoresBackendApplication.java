package vn.gscores;

import org.springframework.boot.SpringApplication;

public class TestGScoresBackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(GScoresBackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
