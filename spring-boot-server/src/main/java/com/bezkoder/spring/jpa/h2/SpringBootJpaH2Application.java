package com.bezkoder.spring.jpa.h2;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.bezkoder.spring.jpa.h2.model.User;
import com.bezkoder.spring.jpa.h2.repository.UserRepository;

@SpringBootApplication
@EnableJpaAuditing
@EnableCaching  // Activates Caffeine cache — cache names declared in application.properties
public class SpringBootJpaH2Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootJpaH2Application.class, args);
	}

	@Bean
	CommandLineRunner init(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.findByUsername("staff").isEmpty()) {
				userRepository.save(new User("staff", passwordEncoder.encode("password123"), "CID Staff"));
			}
			if (userRepository.findByUsername("member").isEmpty()) {
				userRepository.save(new User("member", passwordEncoder.encode("password123"), "Member"));
			}
			if (userRepository.findByUsername("sponsor").isEmpty()) {
				userRepository.save(new User("sponsor", passwordEncoder.encode("password123"), "Sponsor"));
			}
		};
	}
}
