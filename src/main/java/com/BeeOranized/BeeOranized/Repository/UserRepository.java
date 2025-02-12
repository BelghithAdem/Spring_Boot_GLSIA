package com.BeeOranized.BeeOranized.Repository;

import com.BeeOranized.BeeOranized.Entity.Role;
import com.BeeOranized.BeeOranized.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUserEmail(String userEmail);

    Boolean existsByUserEmail(String userEmail);


    List<User> findByRolesContaining(Role role);

}
