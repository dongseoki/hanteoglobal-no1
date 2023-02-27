package com.example.entities;

import lombok.Getter;

@Getter
public class CategoryBoardRalation {
  private Long id;
  private Long categoryId;
  private Long BoardId;

  public CategoryBoardRalation(Long id, Long categoryId, Long boardId) {
    this.id = id;
    this.categoryId = categoryId;
    BoardId = boardId;
  }
}
