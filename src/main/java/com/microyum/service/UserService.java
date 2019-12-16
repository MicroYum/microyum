package com.microyum.service;

import com.microyum.common.http.BaseResponseDTO;
import com.microyum.dto.UserDto;
import com.microyum.model.MyRole;
import com.microyum.model.MyUser;

import java.util.List;

public interface UserService {

    boolean checkUserLogin(String userName, String password);

    MyUser getUserByName(String name);

    BaseResponseDTO createUser(UserDto dto);

    BaseResponseDTO userListOverview(int page, int limit, String name);

    List<MyRole> referRoleList();

    boolean updateUser(UserDto dto);

    boolean deleteUser(Long id);

    List<MyUser> userList();
}
