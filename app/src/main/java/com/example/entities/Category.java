package com.example.entities;

import lombok.Getter;

@Getter
public class Category {
  private Long pid;
  private Long id;
  private String name;
  private static Long newId = 0L;

  public Category(long pid, Long id, String name) {
    this.pid = pid;
    this.id = id;
    this.name = name;
  }

  public Category(Long id, String name) {
    this.pid = null;
    this.id = id;
    this.name = name;
  }
}
