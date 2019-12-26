package com.microyum.dao.jpa;

import com.microyum.model.common.MyArticleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MyArticleTypeDao extends JpaRepository<MyArticleType, Long> {

    @Query(value = "select * from `my_article_type` order by `order` asc", nativeQuery = true)
    List<MyArticleType> findAllArticleType();

    MyArticleType findByName(String name);

}
