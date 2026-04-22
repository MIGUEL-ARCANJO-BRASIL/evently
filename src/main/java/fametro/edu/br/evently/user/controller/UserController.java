package fametro.edu.br.evently.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @GetMapping()
    public String vasco(){
        return "vasco";
    }
}
