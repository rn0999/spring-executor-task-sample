package com.example.demo.controller;

import com.example.demo.model.Job;
import com.example.demo.model.Task;
import com.example.demo.service.TaskExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
public class TaskController {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    @Qualifier("syncDataTaskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    TaskExecutorService taskExecutorService;

    @GetMapping("/api/syncData/{path}")
    public ResponseEntity syncData(@PathVariable("path") String path, @RequestHeader("envId") String envId) {
        try {
            logger.info("Adding Task(path={},envId={})", path, envId);
            taskExecutor.execute(new Task(taskExecutorService::syncData, new Job(path, envId)));
            return ResponseEntity.accepted().build();
        } catch (Exception e) {
            logger.error("Exception while adding task :: {}", e.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(406)).build();
        }
    }

    @GetMapping("/api/getStatus")
    public Object getStatus() {
        return taskExecutorService.getStatus();
    }

    @GetMapping("/api/taskStatus")
    public Object getTaskStatus() {
        ThreadPoolExecutor threadPoolExecutor = taskExecutor.getThreadPoolExecutor();
        int activeCount = threadPoolExecutor.getActiveCount();
        long taskCount = threadPoolExecutor.getTaskCount();
        long completedTaskCount = threadPoolExecutor.getCompletedTaskCount();
        int queueSize = taskExecutor.getQueueSize();
        int queueCapacity = taskExecutor.getQueueCapacity();
        long tasksToDo = taskCount - completedTaskCount - activeCount;
        Map<String, Object> response = new HashMap<>();
        response.put("activeCount", activeCount);
        response.put("taskCount", taskCount);
        response.put("completedTaskCount", completedTaskCount);
        response.put("tasksToDo", tasksToDo);
        response.put("queueSize", queueSize);
        response.put("queueCapacity", queueCapacity);
        return response;
    }
}
