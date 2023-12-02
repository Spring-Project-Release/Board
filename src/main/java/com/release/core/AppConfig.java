package com.release.core;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.release.core.repository.BookmarkRepository;
import com.release.core.repository.CategoryRepository;
import com.release.core.repository.JPACategoryRepository;
import com.release.core.repository.JPAPostRepository;
import com.release.core.repository.JPAPostTagsConnectionRepository;
import com.release.core.repository.JPATagRepository;
import com.release.core.repository.JPAUserRepository;
import com.release.core.repository.JpaBookmarkRepository;
import com.release.core.repository.PostRepository;
import com.release.core.repository.PostTagsConnectionRepository;
import com.release.core.repository.TagRepository;
import com.release.core.repository.UserRepository;
import com.release.core.service.BookmarkService;
import com.release.core.service.PostService;
import com.release.core.service.UserService;
import com.release.core.service.UserServiceImpl;

import jakarta.persistence.EntityManager;

@Configuration
public class AppConfig{

    private final DataSource dataSource;
    private final EntityManager em;


    public AppConfig(DataSource dataSource, EntityManager em) {
        this.dataSource = dataSource;
        this.em = em;
    }
    // User
    @Bean
    public UserService userService() {
        return new UserServiceImpl(userRepository());
    }
    @Bean
    public UserRepository userRepository() {
        //return new MemoryUserRepository();
        //return new JdbcTemplateUserRepository(dataSource);
        return new JPAUserRepository(em);
    }


    // Category
    @Bean
    public CategoryRepository categoryRepository() {return new JPACategoryRepository(em);}

    // Tag
    @Bean
    public TagRepository tagRepository() {return new JPATagRepository(em);}

    // PostTagsConnection
    @Bean
    public PostTagsConnectionRepository postTagsConnectionRepository() {return new JPAPostTagsConnectionRepository(em);}


    // Post
    @Bean
    public PostRepository postRepository() {
        return new JPAPostRepository(em);
    }

    @Bean
    public PostService postService() {return new PostService(postRepository(), postTagsConnectionRepository(), tagRepository(), categoryRepository());}


    // Bookmark
    @Bean
    public BookmarkService bookmarkService() {
      return new BookmarkService(bookmarkRepository());
    }

    @Bean
    public BookmarkRepository bookmarkRepository() {
      return new JpaBookmarkRepository(em);
    }

}
