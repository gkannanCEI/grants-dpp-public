package com.bezkoder.spring.jpa.h2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.bezkoder.spring.jpa.h2.model.User;
import com.bezkoder.spring.jpa.h2.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

  @Autowired
  UserRepository userRepository;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Value("${MOCK_USER_USERNAME:admin}")
  private String mockUsername;

  @Value("${MOCK_USER_PASSWORD:password123}")
  private String mockPassword;

  @Override
  public void run(String... args) throws Exception {
    if (userRepository.findByUsername(mockUsername).isEmpty()) {
      User user = new User(mockUsername, passwordEncoder.encode(mockPassword), "CID Staff");
      userRepository.save(user);
      System.out.println("Mock user created: " + mockUsername);
    }
  }
}