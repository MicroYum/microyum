package com.microyum.dao.jpa;

import com.microyum.model.common.MyUserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyUserRoleDao extends JpaRepository<MyUserRole, Long> {

    void deleteByUserId(Long uid);

    MyUserRole findByUserId(Long uid);
}
