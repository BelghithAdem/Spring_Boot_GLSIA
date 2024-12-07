package com.BeeOranized.BeeOranized.Repository;

import com.BeeOranized.BeeOranized.Entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRipository extends JpaRepository<Task, Long> {
   List<Task> findByProjectId(Long projectId);
}
