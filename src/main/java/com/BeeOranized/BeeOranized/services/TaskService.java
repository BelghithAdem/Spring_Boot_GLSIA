package com.BeeOranized.BeeOranized.services;

import com.BeeOranized.BeeOranized.Entity.Project;
import com.BeeOranized.BeeOranized.Entity.Task;
import com.BeeOranized.BeeOranized.Repository.ProjectRepository;
import com.BeeOranized.BeeOranized.Repository.TaskRipository;
import com.BeeOranized.BeeOranized.enums.taskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRipository taskRepository;
    @Autowired
    private ProjectRepository projectRepository;

    @Transactional
    public Task createTask(Task task) {
        // Ensure the task is associated with a project
        if (task.getProject() == null || task.getProject().getId() == null) {
            return null;
        }

        // Set default status if not provided
        if (task.getStatus() == null) {
            task.setStatus(taskStatus.NEW);
        }

        // Set current date as startDate if not provided
        if (task.getStartDate() == null) {
            task.setStartDate(LocalDate.now());
        }

        // Save the task
        Task createdTask = taskRepository.save(task);

        // Fetch the project from the database and update its task list
        Project project = projectRepository.findById(task.getProject().getId()).orElse(null);
        if (project != null) {
            project.getTasks().add(createdTask);
            projectRepository.save(project);
        }

        return createdTask;
    }

    public Task updateTaskProgress(Task task) {
        // Ensure the task exists
        if (task.getId() == null) {
            return null;
        }

        // Fetch the task from the database
        Task existingTask = taskRepository.findById(task.getId()).orElse(null);
        if (existingTask == null) {
            return null;
        }

        // Update the task status
        existingTask.setStatus(taskStatus.PENDING);
        return taskRepository.save(existingTask);
    }

    public Task updateTaskTesting(Task task) {
        // Ensure the task exists
        if (task.getId() == null) {
            return null;
        }

        Task existingTask = taskRepository.findById(task.getId()).orElse(null);
        if (existingTask == null) {
            return null;
        }

        // Update the task status
        existingTask.setStatus(taskStatus.BLOQUED);
        return taskRepository.save(existingTask);
    }

    public Task updateTaskComplete(Task task) {
        if (task.getId() == null) {
            return null;
        }

        Task existingTask = taskRepository.findById(task.getId()).orElse(null);
        if (existingTask == null) {
            return null;
        }

        existingTask.setStatus(taskStatus.FINISHED);
        return taskRepository.save(existingTask);
    }

    public List<Task> getTasksByProjectId(Long projectId) {
        return taskRepository.findByProjectId(projectId);
    }

    public Task updateTask(Task task) {
        // Ensure the task exists
        if (task.getId() == null) {
            return null;
        }

        Task existingTask = taskRepository.findById(task.getId()).orElse(null);
        if (existingTask == null) {
            return null;
        }

        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setAssignedUser(task.getAssignedUser());
        existingTask.setStartDate(task.getStartDate());
        existingTask.setEndDate(task.getEndDate());
        existingTask.setStatus(task.getStatus());
        return taskRepository.save(existingTask);
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
}