package com.streamcast.entity;

public class Schemas {

  public record UserResponse(String id, String name, String email){
    public UserResponse(User user){
      this(user.getId(), user.getName(), user.getUsername());
    }
  }

  public record RegisterRequest(String name, String email, String password){}

  public record LoginRequest(String email,String password){}

}
