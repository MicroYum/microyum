package com.microyum.dao;

import com.microyum.model.MyBlogLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyBlogLogDao extends JpaRepository<MyBlogLog, Long> {

}
