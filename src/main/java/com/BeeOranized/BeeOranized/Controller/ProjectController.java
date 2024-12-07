package com.BeeOranized.BeeOranized.Controller;

import com.BeeOranized.BeeOranized.Entity.Project;
import com.BeeOranized.BeeOranized.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
//@CrossOrigin("http://localhost:4200/")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    @PostMapping("/project")
    public Project createProject(@RequestBody Project project) {
        return projectService.createProject(project);
    }

    @GetMapping("/project/{scrumMaster}")
    public List<Project> getProjectsByScrumMaster(@PathVariable String scrumMaster) {
        return projectService.getProjectsByScrumMaster(scrumMaster);
    }

    @GetMapping("/project/user/{AssignedUsers}")
    public List<Project> getProjectsByUser(@PathVariable String AssignedUsers) {
        return projectService.getProjectsByAssignedUsers(AssignedUsers);
    }

    @GetMapping("/project/user")
    public List<Project> getProjectsByUser(@RequestParam(required = false) String assignedUsers,
            @RequestParam(required = false) String scrumMaster) {
        if (assignedUsers != null && scrumMaster != null) {
            // Both parameters provided, retrieve projects where the provided user is either
            // the scrum master or an assigned user
            return projectService.getProjectsByAssignedUserOrScrumMaster(assignedUsers, scrumMaster);
        } else if (assignedUsers != null) {
            // Only assignedUsers parameter provided, retrieve projects where the provided
            // user is among the assigned users
            return projectService.getProjectsByAssignedUsers(assignedUsers);
        } else if (scrumMaster != null) {
            // Only scrumMaster parameter provided, retrieve projects where the provided
            // user is the scrum master
            return projectService.getProjectsByScrumMaster(scrumMaster);
        } else {
            // No parameters provided, return empty list or handle error
            return Collections.emptyList();
        }
    }

    @GetMapping("/project")
    public List<Project> getAllProjects() {
        return projectService.getAllProjects();
    }

    @PutMapping("/project")
    public Project updateProject(@RequestBody Project project) {
        return projectService.updateProject(project);
    }

    @DeleteMapping("/project/{id}")
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }

    @PutMapping("/project/{id}")
    public Project updateProject(@PathVariable Long id, @RequestBody Project project) {
        project.setId(id);
        return projectService.updateProjectbyid(id, project);
    }

    @GetMapping("project/project/{id}")
    public Project getProjectById(@PathVariable Long id) {
        return projectService.getProjectById(id);

    }
}
