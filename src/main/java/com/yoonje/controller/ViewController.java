package com.yoonje.controller;

import com.yoonje.model.ToDo;
import com.yoonje.service.Paginator;
import com.yoonje.service.ToDoListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
public class ViewController {

    @Autowired
    private ToDoListService toDoListService;

    @GetMapping(value = "/todos")
    public ModelAndView list(@RequestParam(defaultValue = "1") int currentPage,
                             @RequestParam(defaultValue = "5") int display) {

        Page<ToDo> pagedList = this.toDoListService.list(currentPage, display);
        long totalCount = pagedList.getTotalElements();

        Paginator.Pagination pagination = Paginator.paging(currentPage, display, totalCount);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("pagination", pagination);
        modelAndView.addObject("list", pagedList.getContent());
        modelAndView.setViewName("todolist");

        return modelAndView;
    }

}
