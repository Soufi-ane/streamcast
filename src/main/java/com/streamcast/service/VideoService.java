package com.streamcast.service;

import java.util.Optional;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.streamcast.entity.VideoData;
import com.streamcast.repo.VideoRepo;

@Service 
public class VideoService {
  private final VideoRepo videoRepo;
  private final FileStorageService fileService;

  @Autowired
  public VideoService(VideoRepo videoRepo, FileStorageService fileService){
    this.videoRepo = videoRepo;
    this.fileService = fileService;
  }

  public ResponseEntity<?> upload(MultipartFile file){
    String dbFileName = "";
    try {
      dbFileName = fileService.storeFile(file);
    } catch (Exception ex){
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex);
    }
    VideoData data = new VideoData(file.getOriginalFilename(), dbFileName);
    videoRepo.save(data);
    return ResponseEntity.ok("Video uploaded successfully '" + file.getOriginalFilename() + "'");
  }

  public ResponseEntity<?> streamVideo(String id){
    Optional<VideoData> optData = videoRepo.findById(id);
    if(optData.isPresent()){
      VideoData data = optData.get();
      try {
        Resource vidResource = fileService.loadFileResource(data.getDbName());
        return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + data.getOgName())
          .contentType(MediaType.parseMediaType("video/mp4"))
          .body(vidResource);
      }catch (Exception ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex);
      }
    }else {
      return ResponseEntity.notFound().build();
    }
  }
}
