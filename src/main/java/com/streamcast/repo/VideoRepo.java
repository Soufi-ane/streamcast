package com.streamcast.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.streamcast.entity.VideoData;

@Repository
public interface VideoRepo extends JpaRepository<VideoData, String> {}
