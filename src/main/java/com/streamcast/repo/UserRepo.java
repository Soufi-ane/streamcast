package com.streamcast.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.streamcast.entity.User;

@Repository
public interface UserRepo extends JpaRepository<User, String> {

  public Optional<User> findByEmail(String email);

}
