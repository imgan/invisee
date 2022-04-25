package com.nsi.services.impl;

import com.nsi.controllers.BaseController;
import com.nsi.repositories.core.GlobalParameterRepository;
import com.nsi.services.AttachFileService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class AttachFileServiceImpl extends BaseController implements AttachFileService {
    @Autowired
    GlobalParameterRepository globalParameterRepository;

    public Map uploadToAwsS3(String locationTmpFile, String pathAndFileName){
        try{
            String url = globalParameterRepository.findByCategoryAndName("INTERNAL", "REDIRECT_URL_TO_API_INTERNAL").getValue();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("attachment", new FileSystemResource(locationTmpFile));
            body.add("pathAndFileName", pathAndFileName);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.exchange(url + "/file/upload", HttpMethod.POST, requestEntity, Map.class);
            logger.info("response :"+response);
            return response.getBody();
        }catch(HttpStatusCodeException e){
            logger.error(e.getMessage(), e);
            throw e;
        }
    }

    public Map getFileFromAwsS3(String path){
        try{
            String url = globalParameterRepository.findByCategoryAndName("INTERNAL", "REDIRECT_URL_TO_API_INTERNAL").getValue();
            JSONObject jBody = new JSONObject();
            jBody.put("key", path);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "*/*");
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity entity = new HttpEntity(jBody, headers);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.exchange(url + "/file/download", HttpMethod.POST, entity, Map.class);
            return response.getBody();
        }catch(HttpStatusCodeException e){
            logger.error(e.getMessage(), e);
            e.getResponseBodyAsString();
            throw e;
        }
    }

    public void deleteFileFromAwsS3(String path){
        try{
            String url = globalParameterRepository.findByCategoryAndName("INTERNAL", "REDIRECT_URL_TO_API_INTERNAL").getValue();
            JSONObject jBody = new JSONObject();
            jBody.put("key", path);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "*/*");
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity entity = new HttpEntity(jBody, headers);
            RestTemplate restTemplate = new RestTemplate();
            Object response = restTemplate.exchange(url + "/file/delete", HttpMethod.POST, entity, Object.class);
            logger.info("response deleteFileFromAwsS3 :"+((ResponseEntity) response).getBody());
        }catch(HttpStatusCodeException e){
            logger.error(e.getMessage(), e);
            e.getResponseBodyAsString();
            throw e;
        }
    }
}