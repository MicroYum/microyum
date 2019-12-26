package com.microyum.dao.jpa;

import com.microyum.model.blog.MyBlogLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyBlogLogDao extends JpaRepository<MyBlogLog, Long> {

}
