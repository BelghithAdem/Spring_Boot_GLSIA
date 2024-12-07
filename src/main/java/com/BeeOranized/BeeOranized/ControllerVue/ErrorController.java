package com.BeeOranized.BeeOranized.ControllerVue;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @RequestMapping("/error")
    public String handleError() {
        // Return a view to show an error page
        return "error"; // Can be a custom error page (HTML, JSP, etc.)
    }

    public String getErrorPath() {
        return "/error";
    }
}
