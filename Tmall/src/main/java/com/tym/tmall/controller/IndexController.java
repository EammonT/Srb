package com.tym.tmall.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
public class IndexController {

    @GetMapping("/index")
    public ModelAndView index(ModelAndView modelAndView) throws IOException {
        modelAndView.setViewName("/index.html");
        return modelAndView;
    }
}
