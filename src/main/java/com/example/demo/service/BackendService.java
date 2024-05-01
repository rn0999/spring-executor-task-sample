package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


@Service
public class BackendService {

    Logger logger = LoggerFactory.getLogger(getClass());


    public ResponseEntity<Map> getBackendResponse(String path, String envId){
        RestTemplate restTemplate = new RestTemplate();
        logger.info("Getting Response for path {}, envId {}",path,envId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("envId",envId);
        HttpEntity httpEntity = new HttpEntity(httpHeaders);
        return restTemplate.exchange("http://localhost:8080/api/getResponse/"+path, HttpMethod.GET,httpEntity,Map.class);
    }
}
