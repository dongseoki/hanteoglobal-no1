package com.example.service;

import com.example.dtos.BoardDto;
import com.example.dtos.CategoryDto;
import com.example.dtos.ResponseDto;
import com.example.entities.Board;
import com.example.entities.Category;
import com.example.entities.CategoryBoardRalation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class HashMapCategorySearch implements CategorySearch {
  private Map<Long, List<Category>> categoryParentMap = new HashMap();
  // key : Category의 pid

  private Map<Long, Category> categoryMap = new HashMap();
  // key : Category의 id

  private Map<String, List<Category>> categoryNameMap = new HashMap();
  // key : 카테고리 이름.
  // 완전일치만 허용하도록 하겠습니다.

  private Map<Long, Board> boardMap = new HashMap();
  // key : Board의 id.
  private Map<Long, List<CategoryBoardRalation>> categoryBoardRelationMap = new HashMap();
  // key : CategoryBoardRelation의 CategoryId.

  private Long categoryNewId = 1L;
  private Long boardNewId = 1L;
  private Long categoryBoardNewId = 1L;

  public void pushToCategoryParentMap(Category category) {
    if (!categoryParentMap.containsKey(category.getPid())) {
      categoryParentMap.put(category.getPid(), new ArrayList<>());
    }
    categoryParentMap.get(category.getPid()).add(category);
  }

  public void pushToCategoryBoardRelationMap(CategoryBoardRalation categoryBoardRalation) {
    if (!categoryBoardRelationMap.containsKey(categoryBoardRalation.getCategoryId())) {
      categoryBoardRelationMap.put(categoryBoardRalation.getCategoryId(), new ArrayList<>());
    }
    categoryBoardRelationMap.get(categoryBoardRalation.getCategoryId()).add(categoryBoardRalation);
  }

  public void pushToCategoryNameMap(Category category) {
    if (!categoryNameMap.containsKey(category.getName())) {
      categoryNameMap.put(category.getName(), new ArrayList<>());
    }
    categoryNameMap.get(category.getName()).add(category);

  }

  @Override
  public Category addRootCategory(String name) {
    Category category = new Category(categoryNewId++, name);
    pushToCategoryParentMap(category);
    categoryMap.put(category.getId(), category);
    pushToCategoryNameMap(category);
    return category;
  }

  @Override
  public Category addCategory(String name, long pid) {
    Category category = new Category(pid, categoryNewId++, name);

    pushToCategoryParentMap(category);
    categoryMap.put(category.getId(), category);
    pushToCategoryNameMap(category);
    return category;
  }

  @Override
  public Board addBoard(String name) {
    Board board = new Board(boardNewId++, name);
    boardMap.put(board.getId(), board);
    return board;
  }

  @Override
  public CategoryBoardRalation addCategoryBoardRalation(Category category, Board board) {
    CategoryBoardRalation categoryBoardRalation =
      new CategoryBoardRalation(categoryBoardNewId, category.getId(), board.getId());
    pushToCategoryBoardRelationMap(categoryBoardRalation);
    return categoryBoardRalation;
  }

  @Override
  public List addCategoryList(List<String> names, long pid) {
    List<Category> categoryList = new ArrayList<>();
    for (String name : names
    ) {
      categoryList.add(addCategory(name, pid));
    }
    return categoryList;
  }

  @Override
  public List addBoardList(List<String> names) {
    List<Board> boardList = new ArrayList<>();
    for (String name : names
    ) {
      boardList.add(addBoard(name));
    }
    return boardList;
  }

  @Override
  public List addCategoryAndBoardWithNames(List<String> names, long pid) {
    List<Category> categoryList = new ArrayList<>();
    for (String name : names
    ) {
      Category category = addCategory(name, pid);
      categoryList.add(category);
      Board board = addBoard(name);
      addCategoryBoardRalation(category, board);
    }
    return categoryList;
  }

  @Override
  public List addCategoryBoardRalationList(List<Category> categories,
                                           List<Board> boards) {
    List<CategoryBoardRalation> relationList = new ArrayList<>();
    final int SIZE = categories.size();
    for (int i = 0; i < SIZE; i++) {
      relationList.add(addCategoryBoardRalation(categories.get(i), boards.get(i)));
    }
    return relationList;
  }


  @Override
  public void addCommonBoard(List<Category> commonCategories, Board commonBoard) {
    for (Category category :
      commonCategories) {
      addCategoryBoardRalation(category, commonBoard);
    }
  }

  @Override
  public ResponseDto searchCategoryAndBoardById(Long id) {
    ResponseDto responseDto = new ResponseDto();
    if (!categoryMap.containsKey(id)) {
      return new ResponseDto();
    }
    CategoryDto categoryDto = getCategoryById(id);
    responseDto.setCategoryList(List.of(categoryDto));
    List<CategoryDto> leafNodes = getLeaf(categoryDto);
    Set<BoardDto> boards = getBoards(leafNodes);
    responseDto.setBoardList(boards);

    return responseDto;
  }

  @Override
  public ResponseDto searchCategoryAndBoardByName(String name) {
    ResponseDto responseDto = new ResponseDto();
    if (!categoryNameMap.containsKey(name)) {
      return new ResponseDto();
    }


    List<CategoryDto> categoryList = new ArrayList<>();
    Set<BoardDto> mainBoardList = new HashSet<>();
    for (Category category :
      categoryNameMap.get(name)) {
      CategoryDto categoryDto = getCategoryById(category.getId());
      categoryList.add(categoryDto);
      List<CategoryDto> leafNodes = getLeaf(categoryDto);
      mainBoardList.addAll(getBoards(leafNodes));
    }

    responseDto.setCategoryList(categoryList);
    responseDto.setBoardList(mainBoardList);

    return responseDto;
  }

  private Set<BoardDto> getBoards(List<CategoryDto> leafNodes) {
    Set<BoardDto> boardDtos = new HashSet<>();
    for (CategoryDto leaf :
      leafNodes) {
      for (CategoryBoardRalation cbr :
        categoryBoardRelationMap.get(leaf.getId())) {
        Board board = boardMap.get(cbr.getBoardId());
        boardDtos.add(new BoardDto(board.getId(), board.getName()));
      }
    }
    return boardDtos;
  }

  private List<CategoryDto> getLeaf(CategoryDto categoryDto) {
    List<CategoryDto> leafNodes = new ArrayList<>();
    Queue<CategoryDto> queue = new LinkedList();
    queue.add(categoryDto);
    while (!queue.isEmpty()) {
      CategoryDto here = queue.poll();
      if (here.getChildList() == null) {
        leafNodes.add(here);
      } else {
        for (CategoryDto child :
          here.getChildList()) {
          queue.add(child);
        }
      }
    }
    return leafNodes;
  }

  private CategoryDto getCategoryById(Long id) {
    if (!categoryMap.containsKey(id)) {
      return null;
    }
    Category category = categoryMap.get(id);
    CategoryDto categoryDto = new CategoryDto(category.getId(), category.getName());
    if (categoryParentMap.containsKey(id)) {
      List<CategoryDto> childDtoList = new ArrayList<>();
      for (Category child :
        categoryParentMap.get(id)) {
        childDtoList.add(getCategoryById(child.getId()));
      }
      categoryDto.setChildList(childDtoList);
    }
    return categoryDto;
  }
}
