package com.ticketing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TicketingGatewayAdminMS {

	public static void main(String[] args) {
		SpringApplication.run(TicketingGatewayAdminMS.class, args);
	}

}
