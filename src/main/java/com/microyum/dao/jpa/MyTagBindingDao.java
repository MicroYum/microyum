package com.microyum.dao.jpa;

import com.microyum.model.common.MyTagBinding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface MyTagBindingDao extends JpaRepository<MyTagBinding, Long> {

    int deleteByTagId(Long tagId);

    @Modifying
    @Transactional
    @Query(value = "delete from my_tag_binding where tag_id = ?1 and entity_id = ?2 and category = ?3", nativeQuery = true)
    int deleteEntity(Long tagId, Long entityId, Integer category);

    @Query(value = "select count(1) from my_tag_binding where tag_id = ?1", nativeQuery = true)
    Long findCountByTagId(Long tagId);
}
