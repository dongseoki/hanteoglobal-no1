package com.example.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class CategoryDto {
  private Long id;
  private String name;
  @Setter
  private List<CategoryDto> childList = null;

  public CategoryDto(Long id, String name) {
    this.id = id;
    this.name = name;
  }
}
