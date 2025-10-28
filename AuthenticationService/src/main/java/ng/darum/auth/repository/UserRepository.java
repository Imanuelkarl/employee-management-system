package ng.darum.auth.repository;

import ng.darum.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    //Verify if a user already exists with the given email
    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
