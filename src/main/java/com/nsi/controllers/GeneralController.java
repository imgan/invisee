package com.nsi.controllers;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.nsi.services.AttachFileService;
import com.nsi.util.ConstantUtil;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.nsi.domain.core.AttachFile;
import com.nsi.domain.core.CustomerDocument;
import com.nsi.repositories.core.AgentRepository;
import com.nsi.repositories.core.AttachFileRepository;
import com.nsi.repositories.core.CustomerDocumentRepository;
import com.nsi.services.ChannelService;

@RestController
@RequestMapping("/general")
public class GeneralController extends BaseController {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CustomerDocumentRepository customerDocumentRepository;
    @Autowired
    AgentRepository agentRepository;
    @Autowired
    ChannelService channelService;
    @Autowired
    AttachFileRepository attachFileRepository;
    @Autowired
    AttachFileService attachFileService;

    //TODO: General View Document
    @RequestMapping(value = "/document_view", method = RequestMethod.POST)
    public ResponseEntity<?> viewDocument(HttpServletRequest request, @RequestBody Map map, HttpServletResponse response) {
        String version = request.getHeader("version");
        Map resultMap = new HashMap<>();

        //TODO: Check mandatory field
        if (map.get("key").equals("")) {
            resultMap.put("code", 10);
            resultMap.put("info", "Key can't null");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
        }
        if (map.get("signature").equals("") || map.get("agent").equals("")) {
            resultMap.put("code", 10);
            resultMap.put("info", "Signature or agent can't null");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
        }

        //TODO: Check Signature Agent
        Map checkSignature = channelService.generateAgentSignature(String.valueOf(map.get("agent")), String.valueOf(map.get("signature")));
        if (!checkSignature.get("code").equals(0)) {

            if("2".equals(version)){
                checkSignature = changeCodeIntToString(checkSignature);
            }

            return new ResponseEntity<Map>(checkSignature, HttpStatus.OK);
        }

        CustomerDocument doc = customerDocumentRepository.findByFileKey(String.valueOf(map.get("key")));

        try {
            //TODO: Cek document dengan key yg dilemparkan user terdapat di db atau engga
            if (doc == null) {
                resultMap.put("code", 50);
                resultMap.put("info", "File not found");

                if("2".equals(version)){
                    resultMap = changeCodeIntToString(resultMap);
                }

                return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
            } else {
                Map dataImage = attachFileService.getFileFromAwsS3(doc.getFileLocation());
                if((int) dataImage.get("code") == 50){
                    resultMap.put("code", 50);
                    resultMap.put("info", "File not found");
                    return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
                }

                response.setContentType("APPLICATION/OCTET-STREAM");
                response.setHeader("Content-Disposition", "Attachment;Filename=" + doc.getFileName());
                byte[] content = Base64.decodeBase64(dataImage.get("data").toString());
                InputStream inputStream = new ByteArrayInputStream(content);
                OutputStream outputStream = response.getOutputStream();
                byte[] buffer = new byte[4096];
                int len;

                while ((len = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                }

                outputStream.flush();
                outputStream.close();
                inputStream.close();
                return new ResponseEntity(outputStream, HttpStatus.OK);
            }
        } catch (Exception e) {
        	logger.error("[FATAL]", e);
        }

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
    }

    @RequestMapping(value = "/download", method = RequestMethod.POST)
    public ResponseEntity<?> downloadFile(HttpServletRequest request, @RequestBody Map map, HttpServletResponse response) {
        String version = request.getHeader("version");
        Map resultMap = new HashMap<>();

        //TODO: Check mandatory field
        if (map.get("key").equals("")) {
            resultMap.put("code", 10);
            resultMap.put("info", "Key can't null");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
        }
        if (map.get("signature").equals("") || map.get("agent").equals("")) {
            resultMap.put("code", 10);
            resultMap.put("info", "Signature or agent can't null");

            if("2".equals(version)){
                resultMap = changeCodeIntToString(resultMap);
            }

            return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
        }

        //TODO: Check Signature Agent
        Map checkSignature = channelService.generateAgentSignature(String.valueOf(map.get("agent")), String.valueOf(map.get("signature")));
        if (!checkSignature.get("code").equals(0)) {

            if("2".equals(version)){
                checkSignature = changeCodeIntToString(checkSignature);
            }

            return new ResponseEntity<Map>(checkSignature, HttpStatus.OK);
        }

        AttachFile doc = attachFileRepository.findByKey(String.valueOf(map.get("key")));

        try {
            //TODO: Cek document dengan key yg dilemparkan user terdapat di db atau engga
            if (doc == null) {
                resultMap.put("code", 50);
                resultMap.put("info", "File not found");

                if("2".equals(version)){
                    resultMap = changeCodeIntToString(resultMap);
                }

                return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
            } else {
                Map dataImage = attachFileService.getFileFromAwsS3(doc.getLokasiFile());
                if((int) dataImage.get("code") == 50){
                    resultMap.put("code", 50);
                    resultMap.put("info", "File not found");
                    return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
                }

                response.setContentType("APPLICATION/OCTET-STREAM");
                response.setHeader("Content-Disposition", "Attachment;Filename=" + doc.getNamaFile());
                byte[] content = Base64.decodeBase64(dataImage.get("data").toString());
                InputStream inputStream = new ByteArrayInputStream(content);
                OutputStream outputStream = response.getOutputStream();
                byte[] buffer = new byte[4096];
                int len;

                while ((len = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, len);
                }

                outputStream.flush();
                outputStream.close();
                inputStream.close();
                return new ResponseEntity(outputStream, HttpStatus.OK);
            }
        } catch (Exception e) {
        	logger.error("[FATAL]", e);
            resultMap = errorResponse(ConstantUtil.STATUS_ERROR_SYSTEM, "general download", null);
        }

        if("2".equals(version)){
            resultMap = changeCodeIntToString(resultMap);
        }

        return new ResponseEntity<Map>(resultMap, HttpStatus.OK);
    }
}