package com.BeeOranized.BeeOranized.ControllerVue;

import com.BeeOranized.BeeOranized.Entity.Project;
import com.BeeOranized.BeeOranized.Entity.User;
import com.BeeOranized.BeeOranized.services.ProjectService;
import com.BeeOranized.BeeOranized.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Collections;
import java.util.List;

@Controller
public class ProjectControllerVue {

    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;

    @GetMapping("/projectList")
    public String getAllProjects(Model model) {
        List<Project> projects = projectService.getAllProjects();
        model.addAttribute("projects", projects);
        return "admin/projectlist";
    }

    @GetMapping("/projectVue/{id}")
    public String getProjectById(@PathVariable Long id, Model model) {
        Project project = projectService.getProjectById(id);
        model.addAttribute("project", project);
        return "projects/detail";
    }

    @RequestMapping("/projectVue/new")
    public String showCreateProjectForm(Model model) {
        model.addAttribute("project", new Project());
        List<User> scrumMasters = userService.getAllScrumMasters();
        model.addAttribute("scrumMasters", scrumMasters);
        return "admin/addproject"; // Utiliser le nom de fichier pour le formulaire de création de projet
    }

    @RequestMapping(value = "/projectVue", method = RequestMethod.POST)
    public RedirectView createProject(@ModelAttribute("project") Project project) {
        projectService.createProject(project);
        return new RedirectView("/projectList");
    }

    @RequestMapping("/projectVue/edit/{id}")
    public String showEditProjectForm(@PathVariable Long id, Model model) {
        Project project = projectService.getProjectById(id);
        model.addAttribute("project", project);
        List<User> scrumMasters = userService.getAllScrumMasters();
        model.addAttribute("scrumMasters", scrumMasters);
        return "admin/updateproject";
    }

    @RequestMapping("/projectVue/update/{id}")
    public String updateProject(@PathVariable Long id, @ModelAttribute("project") Project project) {
        projectService.updateProjectbyid(id, project);
        return "redirect:/projectList"; // Redirect after update

    }

    @GetMapping("/projectVue/delete/{id}")
    public String deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return "redirect:/projectList"; // Redirection après suppression
    }

    @GetMapping("/projectVue/search")
    public String searchProjects(
            @RequestParam(required = false) String assignedUsers,
            @RequestParam(required = false) String scrumMaster,
            Model model) {
        List<Project> projects;
        if (assignedUsers != null && scrumMaster != null) {
            projects = projectService.getProjectsByAssignedUserOrScrumMaster(assignedUsers, scrumMaster);
        } else if (assignedUsers != null) {
            projects = projectService.getProjectsByAssignedUsers(assignedUsers);
        } else if (scrumMaster != null) {
            projects = projectService.getProjectsByScrumMaster(scrumMaster);
        } else {
            projects = Collections.emptyList();
        }
        model.addAttribute("projects", projects);
        return "projects/search";
    }
}
