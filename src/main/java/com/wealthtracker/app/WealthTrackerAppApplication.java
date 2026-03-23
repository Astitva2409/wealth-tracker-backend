package com.wealthtracker.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WealthTrackerAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(WealthTrackerAppApplication.class, args);
	}
}
