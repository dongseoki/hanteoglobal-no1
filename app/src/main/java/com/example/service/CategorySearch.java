package com.example.service;

import com.example.dtos.ResponseDto;
import com.example.entities.Board;
import com.example.entities.Category;
import com.example.entities.CategoryBoardRalation;

import java.util.List;

public interface CategorySearch {
  Category addRootCategory(String name);

  Category addCategory(String name, long pid);

  Board addBoard(String name);

  List addCategoryList(List<String> names, long pid);

  CategoryBoardRalation addCategoryBoardRalation(Category category, Board board);


  List addCategoryAndBoardWithNames(List<String> names, long pid);

  void addCommonBoard(List<Category> commonCategories, Board commonBoard);

  ResponseDto searchCategoryAndBoardById(Long id);

  ResponseDto searchCategoryAndBoardByName(String name);
}
