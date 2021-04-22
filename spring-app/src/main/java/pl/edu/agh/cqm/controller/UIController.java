package pl.edu.agh.cqm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {

    @GetMapping({"/", "{_:^(?!api|.*\\.).*$}"})
    public String index() {
        return "forward:/index.html";
    }
}
