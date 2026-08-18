package com.streamcast.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoData {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String ogName;
  private String dbName;

  public VideoData(String ogName, String dbName){
    this.ogName = ogName;
    this.dbName = dbName;
  }

}

