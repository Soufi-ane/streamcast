package com.streamcast.service;

import java.util.Optional;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.streamcast.entity.VideoData;
import com.streamcast.repo.VideoRepo;

@Service 
public class VideoService {
  private static final long CHUNK_SIZE = 1024 * 1024 / 2;
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

  public ResponseEntity<ResourceRegion> streamVideo(String id, HttpRange range){
    Optional<VideoData> optData = videoRepo.findById(id);
    if(optData.isPresent()){
      VideoData data = optData.get();
      try {
        Resource vidResource = fileService.loadFileResource(data.getDbName());

        long contentLength = vidResource.contentLength();
        if(range == null) {
          long defaultEnd = Math.min(CHUNK_SIZE - 1, contentLength - 1);
          range = HttpRange.createByteRange(0, defaultEnd);
        }
        long start = range.getRangeStart(contentLength);
        long end = range.getRangeEnd(contentLength);
        long rangeLength = Math.min(CHUNK_SIZE, end - start + 1);
        ResourceRegion region = new ResourceRegion(vidResource, start, rangeLength);

        return ResponseEntity
          .status(HttpStatus.PARTIAL_CONTENT)
          .contentType(MediaType.parseMediaType("video/mp4"))
          .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
          .header(HttpHeaders.ACCEPT_RANGES, "bytes")
          .body(region);

      }catch (Exception ex){
        throw new RuntimeException("Error loading video");
      }
    }else {
      throw new RuntimeException("not found");
    }
  }
}
