package be.ugent.gsr.financien.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class HomeController {

    //TODO idk iets nuttig zetten ipv dit
    @GetMapping("/")
    @ResponseBody
    public String index() {
        return "Hello World!";
    }

}
