package com.example.demo.service;

import com.example.demo.model.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class TaskExecutorService {

    @Autowired
    BackendService backendService;

    Logger logger = LoggerFactory.getLogger(getClass());
    Map<String,String> refreshMap = new HashMap<>();
    Set<String> beingRefreshed = new LinkedHashSet<>();
    public void syncData(Job job){
        String key = getKey(job);
        if(refreshMap.containsKey(key)){
            if(getTimeDifference(refreshMap.get(key))){
                logger.info("REFRESH (path={},envId={})",job.getPath(),job.getEnvId());
                refreshData(key,job.getPath(), job.getEnvId());
            }else{
                logger.info("NO REFRESH (path={},envId={})",job.getPath(),job.getEnvId());
            }
        }else{
            logger.info("NEW-REFRESH (path={},envId={})",job.getPath(),job.getEnvId());
            refreshData(key,job.getPath(), job.getEnvId());
        }
    }

    public void refreshData(String key, String path,String envId){
        beingRefreshed.add(key);
        ResponseEntity<Map> backendResponse = backendService.getBackendResponse(path, envId);
        Map<String,String> response = backendResponse.getBody();
        refreshMap.put(key,response.get("timestamp"));
        beingRefreshed.remove(key);
    }

    public Boolean getTimeDifference(String timestamp){
        Instant instant1 = Instant.parse(timestamp);
        Instant instant2 = Instant.now();
        long diff = instant2.getEpochSecond() - instant1.getEpochSecond();
        return diff > 120;
    }

    public String getKey(Job job){
        return job.getPath()+"-"+job.getEnvId();
    }

    public Object getStatus(){
        Map<String,Object> response = new HashMap<>();
        response.put("refreshMap",refreshMap);
        response.put("beingRefreshed",beingRefreshed);
        return response;
    }
}
