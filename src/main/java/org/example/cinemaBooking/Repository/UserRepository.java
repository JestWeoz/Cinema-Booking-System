package org.example.cinemaBooking.Repository;


import org.example.cinemaBooking.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findUserEntityByUsername(String username);

    boolean existsByUsername(String username);
}
