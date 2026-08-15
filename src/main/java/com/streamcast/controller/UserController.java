package com.streamcast.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.streamcast.entity.User;
import com.streamcast.entity.Schemas.LoginRequest;
import com.streamcast.entity.Schemas.RegisterRequest;
import com.streamcast.entity.Schemas.UserResponse;
import com.streamcast.service.UserService;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/users")
public class UserController {
  private final UserService userService;

  @Autowired
  public UserController(UserService userService){
    this.userService = userService;
  }

  @PostMapping("/register")
  public ResponseEntity<?> register(@RequestBody RegisterRequest request){
    System.out.println("hit");
    return userService.register(request);
  }

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response){
    return userService.login(request,response);
  }

  @PostMapping("/logout")
  public ResponseEntity<?> logout(HttpServletResponse response){
    return userService.logout(response);
  }
}

