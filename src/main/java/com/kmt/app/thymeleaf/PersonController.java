package com.kmt.app.thymeleaf;

import org.springframework.stereotype.Controller;

@Controller
public class PersonController {
    String getPeople() {
        return "people";
    }
}
