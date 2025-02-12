package com.BeeOranized.BeeOranized.ControllerVue;

import com.BeeOranized.BeeOranized.Entity.Project;
import com.BeeOranized.BeeOranized.Entity.Task;
import com.BeeOranized.BeeOranized.Entity.User;
import com.BeeOranized.BeeOranized.services.ProjectService;
import com.BeeOranized.BeeOranized.services.TaskService;
import com.BeeOranized.BeeOranized.services.UserService;
import com.BeeOranized.BeeOranized.specification.StatusFilter;
import com.BeeOranized.BeeOranized.specification.TaskFilter;
import com.BeeOranized.BeeOranized.specification.TitleFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class taskControllerVue {

    @Autowired
    private TaskService taskService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectService projectService;

    @GetMapping("/tasklist")
    public String getAllTasks(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "status", required = false) String status,
            Model model) {

        List<Task> tasks = taskService.getAllTasks();
        List<TaskFilter> filters = new ArrayList<>();

        if (title != null && !title.isEmpty()) {
            filters.add(new TitleFilter(title));
        }
        if (status != null && !status.isEmpty()) {
            filters.add(new StatusFilter(status));
        }

        for (TaskFilter filter : filters) {
            tasks = filter.apply(tasks);
        }

        model.addAttribute("tasks", tasks);
        model.addAttribute("title", title);
        model.addAttribute("status", status);
        return "admin/tasklist";
    }

    @RequestMapping("/taskVue/new")
    public String createTask(Model model) {
        model.addAttribute("task", new Task());
        List<User> members = userService.getAllMembers();
        model.addAttribute("members", members);
        List<Project> project = projectService.getAllProjects();
        model.addAttribute("project", project);
        return "admin/addtask";
    }

    @RequestMapping(value = "/tasktVue", method = RequestMethod.POST)
    public RedirectView createTasks(@ModelAttribute("task") Task task) {
        taskService.createTask(task);
        return new RedirectView("/tasklist");
    }

    @GetMapping("/taskVue/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "redirect:/tasklist";
    }

    @RequestMapping("/taskVue/edit/{id}")
    public String updateTask(@PathVariable Long id, Model model) {
        Task task = taskService.getTaskById(id);
        model.addAttribute("task", task);
        List<User> members = userService.getAllMembers();
        model.addAttribute("members", members);
        List<Project> project = projectService.getAllProjects();
        model.addAttribute("project", project);
        return "admin/updatetask";
    }

    @RequestMapping("/taskVue/update/{id}")
    public String updateTask(@PathVariable Long id, @ModelAttribute("task") Task task) {
        taskService.updateTaskById(id, task);
        return "redirect:/tasklist";

    }

}
