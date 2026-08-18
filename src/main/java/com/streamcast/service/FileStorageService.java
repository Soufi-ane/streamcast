package com.streamcast.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;

@Service
public class FileStorageService {
  @Value("${file.upload-dir}")
  private String uploadDirStr;

  private Path uploadPath;

  @PostConstruct
  public void init(){
    try {
      uploadPath = Paths.get(uploadDirStr).toAbsolutePath().normalize();
      Files.createDirectories(uploadPath);
    }catch (IOException ex){
      throw new RuntimeException("Coudn't create storage dir\n", ex);
    }
  }

}
