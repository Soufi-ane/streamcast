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

  public String storeFile(MultipartFile file){
    try {
      String ogFileName = file.getOriginalFilename();
      String extension = "";
      if(ogFileName != null && ogFileName.contains(".")){
        extension = ogFileName.substring(ogFileName.lastIndexOf("."));
      }
      String uniqueFileName = UUID.randomUUID().toString() + extension;
      Path saveLocation = uploadPath.resolve(uniqueFileName);
      Files.copy(file.getInputStream(), saveLocation, StandardCopyOption.REPLACE_EXISTING);
      return uniqueFileName;
    }catch (IOException ex) {
      throw new RuntimeException("Coudn't save file\n", ex);
    }
  }

  public Resource loadFileResource(String fileName){
    try {
      Path filePath = uploadPath.resolve(fileName).normalize();
      Resource resource = new UrlResource(filePath.toUri());
      if(resource.exists() && resource.isReadable()){
        return resource;
      }else {
        throw new RuntimeException("File not found or doesn't have read permission\n");
      }
    }catch(IOException ex){
      throw new RuntimeException("Coudn't read file\n", ex);
    }
  }

}
