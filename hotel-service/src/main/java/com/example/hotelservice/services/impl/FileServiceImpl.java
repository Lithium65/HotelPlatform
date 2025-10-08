package com.example.hotelservice.services.impl;

import com.example.hotelservice.services.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public String storeFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String uuidFile = UUID.randomUUID().toString();
        String resultFilename = uuidFile + '.' + file.getOriginalFilename();
        File uploadDir = new File(uploadPath);

        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        file.transferTo(new File(uploadDir, resultFilename));
        return resultFilename;
    }
}