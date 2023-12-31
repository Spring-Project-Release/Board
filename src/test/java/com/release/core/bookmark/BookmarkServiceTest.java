package com.release.core.bookmark;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.release.core.domain.Bookmark;
import com.release.core.repository.BookmarkRepository;
import com.release.core.service.BookmarkService;

@SpringBootTest
@Transactional
class BookmarkServiceTest {

  @Autowired BookmarkService bookmarkService;
  @Autowired BookmarkRepository bookmarkRepository;
  
  @Test
  void 북마크등록() {
    //given
    Bookmark bookmark = new Bookmark();
    bookmark.setPostId(5L);
    bookmark.setUserId(5L);
    
    //when
    Long saveId = bookmarkService.saveOne(bookmark);

    //then
    // Bookmark findBookmark = bookmarkService.findOne(bookmark.getUserId(), bookmark.getPostId()).get();
    Bookmark findBookmark = bookmarkService.findOne(saveId).get();
    assertEquals(bookmark.getBookmarkId(), findBookmark.getBookmarkId());
  }

  @Test
  void 북마크삭제() {
    //then
    bookmarkService.deleteOne(3L);
  }

  @Test
  void 북마크검색() {
    //given
    Bookmark bookmark1 = new Bookmark();
    bookmark1.setUserId(1L);
    bookmark1.setPostId(1L);

    Bookmark bookmark2 = new Bookmark();
    bookmark2.setUserId(1L);
    bookmark2.setPostId(1L);

    //when
    bookmarkService.saveOne(bookmark1);
    IllegalStateException e = assertThrows(IllegalStateException.class, () -> bookmarkService.saveOne(bookmark2));
    
    //then
    assertThat(e.getMessage()).isEqualTo("이미 북마크에 등록된 게시물입니다.");
  }

  // @Test
  void 북마크조회() {
    //given

    //when
    
    //then
  }
}
