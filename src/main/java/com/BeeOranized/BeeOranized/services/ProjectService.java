package com.BeeOranized.BeeOranized.services;

import com.BeeOranized.BeeOranized.Entity.Project;
import com.BeeOranized.BeeOranized.Repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    @Autowired
    private EmailService emailService;
    @Autowired
    private ProjectRepository projectRepository;

    @Transactional
    public Project createProject(Project project) {
        Project createdProject = projectRepository.save(project);
        sendProjectNotification(createdProject);
        return createdProject;
    }

    private void sendProjectNotification(Project project) {
        List<String> assignedUsers = project.getAssignedUsers();
        String subject = "New Project Created: " + project.getTitle();
        String message = "Dear User,\n\nA new project has been created: " + "<strong>" + project.getTitle()
                + "</strong>" +
                "\nYou are assigned to this project as a team member.\n\nProject Details:\n" +
                "Description: " + "<strong>" + project.getDescription() + "</strong>" + "\nStart Date: " + "<strong>"
                + project.getStartDate() +
                "</strong>" + "\nEnd Date: " + "<strong>" + project.getEndDate() + "</strong>"
                + "\n\nRegards,\nYour Team";
        for (String userEmail : assignedUsers) {
            emailService.sendEmail(userEmail, subject, message);
        }
    }

    public List<Project> getProjectsByScrumMaster(String scrumMaster) {
        return projectRepository.findByScrumMaster(scrumMaster);
    }

    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }


    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    public List<Project> getProjectsByAssignedUsers(String assignedUsers) {
        return projectRepository.findByAssignedUser(assignedUsers);
    }

    public List<Project> getProjectsByAssignedUserOrScrumMaster(String assignedUsers, String scrumMaster) {
        return projectRepository.findByAssignedUsersContainingOrScrumMaster(assignedUsers, scrumMaster);
    }

    public Project updateProjectbyid(Long id, Project project) {
        Optional<Project> existingProjectOptional = projectRepository.findById(id);
        if (existingProjectOptional.isPresent()) {
            Project existingProject = existingProjectOptional.get();
            existingProject.setTitle(project.getTitle());
            existingProject.setDescription(project.getDescription());
            existingProject.setScrumMaster(project.getScrumMaster());
            existingProject.setStartDate(project.getStartDate());
            existingProject.setEndDate(project.getEndDate());
            existingProject.setStatus(project.getStatus());
            return projectRepository.save(existingProject);
        } else {
            throw new EntityNotFoundException("Le projet avec l'ID " + id + " n'existe pas.");
        }
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

}
