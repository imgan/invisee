package com.nsi.services;

import java.util.Map;

public interface AttachFileService {
    public Map uploadToAwsS3(String locationTmpFile, String pathAndFileName);
    public Map getFileFromAwsS3(String path);
    public void deleteFileFromAwsS3(String path);
}
