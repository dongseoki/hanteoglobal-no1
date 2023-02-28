# hanteoglobal-no1

* 카테고리는 단 하나의 부모를 가지고 있습니다.
* 카테고리는 여러 자식을 가질수 있습니다.
* 이러한 관계를 다음과 같이 표현할수 있다고 생각했습니다.
* 또한 RDBMS에 저장가능한 형태이여야한다고 생각했습니다.

# 자료구조.

## 카테고리

Category

* pid : 부모 카테고리의 고유 식별자. NULL일 수 있다.(NULL일 경우 최상위 부모를 의미.)
* id : 자신의 고유 식별자. NULL 일수 없다.
* name : 카테고리 이름.

## 게시판

Board

* id : 게시판의 고유 식별자. NULL 일 수 없다.
* name : 게시판 이름.

## 카테고리_게시판 자료구조

CategoryBoardRelation

* id : 관계에 대한 식별자.
* CategoryId : 연결된 카테고리 id
* BoardId : 연결된 게시판 id

# 검색은 어떻게 할 것인가?

* 검색은 카테고리 데이터를 불러올때, category는 pid, 카테고리_게시판 자료구조는 CategoryId를 이용하여 논클러스터드 인덱스를 이용함함을 가정하겠습니다.
* 클러스터디 인덱스인 기본키도 되어있음을 가정.
* 따라서 불러온 데이터를 빠르게 찾기 위해 HashMap을 이용할 것입니다.

따라서 최종적인 자료구조는 다음과 같습니다.

```jave
  private Map<Long, List<Category>> categoryParentMap = new HashMap();
// key : Category의 pid
Map<String, List<Category>> categoryNameMap = new HashMap();
// key : 카테고리 이름.
// 완전일치만 허용하도록 하겠습니다.

Map<Long, Board> BoardMap = new HashMap();
// key : Board의 id.

Map<Long, List<CategoryBoardRelation>> CategoryBoardRelation = new HashMap();
// key : CategoryBoardRelation의 CategoryId.
```

* 이렇게 한다면, 식별자 검색시, O(1) * 하위항목의개수 의 시간내에 찾을수 있습니다.
* 이렇게 한다면, 카테고리명으로 검색할 경우, O(1) * 검색된 카테고리들과 그 하위항목의수 의 시간내에 찾을 수 있습니다.
  이들을 한번더 추상화하여, 검색 기능을 제공하는 클래스를 만들었습니다.

# 임의의 자료구조 구조화

저는 임의의 자료구조로 구조화 하기 위해, 추가적인 자료구조를 사용하겠습니다.

```java
CategoryDto
  *id
  *name
  *List<CategoryDto>childList // null 가능. null 일경우 말단 노드.

  BoardDto
  *CategoryId
  *id
  *name

  ResponseDto={
  "category":[{
  }
  ]
  "board":[{

  },{
  }]

  }
```

# 결론

위 모든 개념을 통합하고, 추상화하여 요구사항을 만족하는 클래스를 만들겠습니다.

```java
class HashMapCategorySearch {

  public Category addCategory(String name, Long pid) {
        ...
  }

  public Board addBoard(String name, Long id) {
        ...
  }

  public CategoryBoardRelation addCategoryBoardRalation(Long categoryId, Long boardId) {
        ...
  }

  public void addCommonBoardRelation(List<Integer> categories, Long boardId) {
        ...
  }

  public ResponseDto searchCategoryAndBoardById(Long id) {
        ...
  }

  public ResponseDto searchCategoryAndBoardById(String name) {
        ...
  }

}
```

이를 한번더 추상화시키겠습니다.

```java
interface CategorySearch {
  public Category addCategory(String name, Long pid);

  public Board addBoard(String name, Long id);

  public CategoryBoardRalation addCategoryBoardRalation(Long categoryId, Long boardId);

  public void addCommonBoard(List<Integer> categories, Long boardId);

  public ResponseDto searchCategoryAndBoardById(Long id);

  public ResponseDto searchCategoryAndBoardById(String name);

}
```

* 추상화함으로써 좀더 나은 방법이 발견될 경우, 새로운 구현체를 만들면 될것입니다.
* 이름이 길어졌지만, 풀네임으로 작성했습니다.
* 해당 부분은 리팩토링과 약자 사전을 팀 내에서 공유함으로써 빠른 리팩토링 가능하다고 판단하였습니다.

# 확장 가능성

* 카테고리가 여러 부모를 갖고, 여러 자식을 가질 수 있다면?
* 중간에 관계를 표시하는 자료구조를 추가할수 있습니다.(관계 테이블)