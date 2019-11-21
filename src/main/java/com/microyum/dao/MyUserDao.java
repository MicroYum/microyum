package com.microyum.dao;

import com.microyum.model.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyUserDao extends JpaRepository<MyUser, Long> {

    MyUser findByNameAndPassword(String name, String password);

    MyUser findByName(String name);
}
