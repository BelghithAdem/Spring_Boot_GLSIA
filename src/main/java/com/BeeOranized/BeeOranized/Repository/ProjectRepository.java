package com.BeeOranized.BeeOranized.Repository;

import com.BeeOranized.BeeOranized.Entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    <Optional> List<Project> findByScrumMaster(String scrumMaster);

    // <Optional> List<Project> findByAssignedUsersContaining(String AssignedUsers);
    <Optional> List<Project> findByAssignedUsersContainingOrScrumMaster(String assignedUsers, String scrumMaster);

    @Query("SELECT p FROM Project p JOIN p.assignedUsers a WHERE LOWER(a) LIKE LOWER(CONCAT('%', :assignedUser, '%'))")
    List<Project> findByAssignedUser(@Param("assignedUser") String assignedUser);
}
