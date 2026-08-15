package com.streamcast.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.streamcast.repo.UserRepo;

@Configuration
public class ApplicationConfig {

  private final UserRepo userRepo;

  public ApplicationConfig(UserRepo userRepo){
    this.userRepo = userRepo;
  }

  @Bean 
  public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
  }

  @Bean
  public UserDetailsService userDetailsService(){
    return identifier -> userRepo.findByEmail(identifier)
      .orElseThrow(()-> new UsernameNotFoundException("User not found"));
  }

}
