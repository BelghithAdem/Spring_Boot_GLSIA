package com.BeeOranized.BeeOranized.services;

import com.BeeOranized.BeeOranized.Entity.Project;
import com.BeeOranized.BeeOranized.Entity.Task;
import com.BeeOranized.BeeOranized.Repository.ProjectRepository;
import com.BeeOranized.BeeOranized.Repository.TaskRipository;
import com.BeeOranized.BeeOranized.enums.taskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    private TaskRipository taskRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Transactional
    public Task createTask(Task task) {
        if (task.getProject() == null || task.getProject().getId() == null) {
            return null;
        }
        if (task.getStatus() == null) {
            task.setStatus(taskStatus.NEW);
        }
        if (task.getStartDate() == null) {
            task.setStartDate(LocalDate.now());
        }
        Task createdTask = taskRepository.save(task);
        Project project = projectRepository.findById(task.getProject().getId()).orElse(null);
        if (project != null) {
            project.getTasks().add(createdTask);
            projectRepository.save(project);
        }
        return createdTask;
    }


    public boolean deleteTask(Long id) {
        Task existingTask = taskRepository.findById(id).orElse(null);
        if (existingTask == null) {
            return false;
        }

        Project project = projectRepository.findById(existingTask.getProject().getId()).orElse(null);
        if (project != null) {
            project.getTasks().remove(existingTask);
            projectRepository.save(project);
        }

        taskRepository.deleteById(id);
        return true;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    public Task updateTaskById(Long id, Task task) {
        Optional<Task> existingTaskOptional = taskRepository.findById(id);
        if (existingTaskOptional.isPresent()) {
            Task existingTask = existingTaskOptional.get();
            existingTask.setTitle(task.getTitle());
            existingTask.setDescription(task.getDescription());
            existingTask.setAssignedUser(task.getAssignedUser());
            existingTask.setStartDate(task.getStartDate());
            existingTask.setEndDate(task.getEndDate());
            existingTask.setStatus(task.getStatus());
            return taskRepository.save(existingTask);
        } else {
            throw new EntityNotFoundException("La tâche avec l'ID " + id + " n'existe pas.");
        }
    }

}