package com.example.demo.model;

public class Job {

    private String path;
    private String envId;

    public Job(String path, String envId) {
        this.path = path;
        this.envId = envId;
    }

    public String getPath() {
        return path;
    }

    public String getEnvId() {
        return envId;
    }
}
