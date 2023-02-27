package com.example.dtos;

import lombok.Getter;

import java.util.Objects;

@Getter
public class BoardDto {
  private Long id;
  private String name;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof BoardDto boardDto)) {
      return false;
    }

    if (!Objects.equals(id, boardDto.id)) {
      return false;
    }
    return Objects.equals(name, boardDto.name);
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    return result;
  }

  public BoardDto(Long id, String name) {
    this.id = id;
    this.name = name;
  }
}
