package com.cat.itacademy.s05.blackjack;

import com.cat.itacademy.s05.blackjack.utils.BlackjackHelper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class S05BlackjackApplication {

	public static void main(String[] args) {
		SpringApplication.run(S05BlackjackApplication.class, args);
	}

	@Bean
	public BlackjackHelper getBlackjackHelper(){
		return new BlackjackHelper();
	}

}
