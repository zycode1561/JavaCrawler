package com.zycode.demo.controller;

import com.zycode.demo.task.ItemTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {

    @Autowired
    private ItemTask itemTask;

    @RequestMapping("/doTask")
    public void doTask() throws Exception {
        itemTask.itemTask();
    }
}
