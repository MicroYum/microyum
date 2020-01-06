package com.microyum.dao.jpa;

import com.microyum.model.common.MyTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MyTagDao extends JpaRepository<MyTag, Long> {

    @Query(value = "select * from my_tag where id = ?1", nativeQuery = true)
    MyTag findByTagId(Long id);
}
