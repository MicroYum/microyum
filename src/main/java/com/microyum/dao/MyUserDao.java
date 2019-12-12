package com.microyum.dao;

import com.microyum.model.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MyUserDao extends JpaRepository<MyUser, Long> {

    MyUser findByNameAndPassword(String name, String password);

    MyUser findByName(String name);

    @Query(value = "select * from my_user where id = ?1", nativeQuery = true)
    MyUser findByUId(Long id);

}
