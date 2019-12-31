package com.microyum.dao.jpa;

import com.microyum.model.common.MyRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface MyRoleDao extends JpaRepository<MyRole, Long> {

    @Query(value = "select `name` from my_user_role ur, my_role r where ur.role_id = r.id and ur.user_id = ?1", nativeQuery = true)
    String findRoleNameByUserId(Long userId);
}
