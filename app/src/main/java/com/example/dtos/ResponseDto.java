package com.example.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ResponseDto {
  List<CategoryDto> categoryList = new ArrayList<>();
  Set<BoardDto> boardList = new HashSet<>();
}
