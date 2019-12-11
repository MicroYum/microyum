package com.microyum.dao;

import com.microyum.model.MyUserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyUserRoleDao extends JpaRepository<MyUserRole, Long> {

    void deleteByUserId(Long uid);

    MyUserRole findByUserId(Long uid);
}
