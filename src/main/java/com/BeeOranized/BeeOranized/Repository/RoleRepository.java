package com.BeeOranized.BeeOranized.Repository;

import java.util.Optional;

import com.BeeOranized.BeeOranized.Entity.ERole;
import com.BeeOranized.BeeOranized.Entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(ERole name);
}