package com.streamcast.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.streamcast.service.VideoService;

@RestController
@RequestMapping("/videos")
public class VideoController {
  private final VideoService videoService;

  @Autowired
  public VideoController(VideoService videoService){
    this.videoService = videoService;
  }

  @PostMapping("upload")
  public ResponseEntity<?> uploadViedo(@RequestParam("file") MultipartFile file){
    if(file.isEmpty()){
      return ResponseEntity.badRequest().body("No file was uploaded");
    }
    String contentType = file.getContentType();
    if(contentType == null || !contentType.startsWith("video/")){
      return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
        .body("Unsupported file type");
    }
    return videoService.upload(file);
  }

}

