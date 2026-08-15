package com.streamcast.service;

import java.time.Duration;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.streamcast.entity.User;
import com.streamcast.entity.Schemas.LoginRequest;
import com.streamcast.entity.Schemas.RegisterRequest;
import com.streamcast.entity.Schemas.UserResponse;
import com.streamcast.repo.UserRepo;
import jakarta.servlet.http.HttpServletResponse;

@Service 
public class UserService {
  private final UserRepo userRepo;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  @Autowired
  public UserService(
    UserRepo userRepo,
    PasswordEncoder passwordEncoder,
    JwtService jwtService
  ){
    this.userRepo = userRepo;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  public ResponseEntity<?> register(RegisterRequest request){
    Optional<User> optUser = userRepo.findByEmail(request.email());
    if(optUser.isPresent()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email taken");
    }
    User user = new User(request, passwordEncoder.encode(request.password()));
    return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse(userRepo.save(user)));
  }

  public ResponseEntity<?> login(LoginRequest request, HttpServletResponse response){
    Optional<User> optUser = userRepo.findByEmail(request.email());
    if(!optUser.isPresent()){
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid username or password");
    } 
    User user = optUser.get();
    if(!passwordEncoder.matches(request.password(), user.getPassword())){
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid username or password");
    }

    String token = jwtService.generateToken(user.getEmail(), user.getId());
    ResponseCookie cookie = ResponseCookie.from("jwt",token)
      .httpOnly(true)
      .secure(false) // dev
      .path("/")
      .maxAge(Duration.ofDays(1))
      .sameSite("Strict")
      .build();

    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    
    return ResponseEntity.ok(new UserResponse(user));
  }

  public ResponseEntity<?> logout(HttpServletResponse response) {
    ResponseCookie cookie = ResponseCookie.from("jwt","")
      .httpOnly(true)
      .secure(false) // dev
      .path("/")
      .maxAge(0)
      .sameSite("Strict")
      .build();
    
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    return ResponseEntity.ok().body("Logged out successfully");
  }

}
