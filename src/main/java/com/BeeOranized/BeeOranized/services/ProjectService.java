package com.BeeOranized.BeeOranized.services;

import com.BeeOranized.BeeOranized.Entity.Project;
import com.BeeOranized.BeeOranized.Repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class ProjectService {
    @Autowired
    private EmailService emailService;
    @Autowired
    private ProjectRepository projectRepository;

    @Transactional
    public Project createProject(Project project) {
        Project createdProject = projectRepository.save(project);

        // Send email notification to assigned users
        sendProjectNotification(createdProject);

        return createdProject;
    }

    private void sendProjectNotification(Project project) {
        // Fetch assigned users' email addresses from the project entity
        List<String> assignedUsers = project.getAssignedUsers();

        // Prepare email content
        String subject = "New Project Created: " + project.getTitle();
        String message = "Dear User,\n\nA new project has been created: " + "<strong>" + project.getTitle()
                + "</strong>" +
                "\nYou are assigned to this project as a team member.\n\nProject Details:\n" +
                "Description: " + "<strong>" + project.getDescription() + "</strong>" + "\nStart Date: " + "<strong>"
                + project.getStartDate() +
                "</strong>" + "\nEnd Date: " + "<strong>" + project.getEndDate() + "</strong>"
                + "\n\nRegards,\nYour Team";

        // Send email to each assigned user
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

    public Project updateProject(Project project) {
        return projectRepository.save(project);
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    /*
     * public List<Project> getProjectsByUser(String user){
     * return projectRepository.findByAssignedUsersContaining(user);
     * }
     */
    public List<Project> getProjectsByUserorScrumMaster(String assignedUsers, String scrumMaster) {
        return projectRepository.findByAssignedUsersContainingOrScrumMaster(assignedUsers, scrumMaster);
    }

    public List<Project> getProjectsByAssignedUsers(String assignedUsers) {
        return projectRepository.findByAssignedUser(assignedUsers);
    }

    public List<Project> getProjectsByAssignedUserOrScrumMaster(String assignedUsers, String scrumMaster) {
        return projectRepository.findByAssignedUsersContainingOrScrumMaster(assignedUsers, scrumMaster);
    }

    public Project updateProjectbyid(Long id, Project project) {
        project.setId(id);
        return projectRepository.save(project);
    }

    public Project getProjectById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

}
