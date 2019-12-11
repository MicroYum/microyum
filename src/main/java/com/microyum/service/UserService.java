package com.microyum.service;

import com.microyum.common.http.BaseResponseDTO;
import com.microyum.dto.UserDTO;
import com.microyum.model.MyRole;
import com.microyum.model.MyUser;

import java.util.List;

public interface UserService {

    boolean checkUserLogin(String userName, String password);

    MyUser getUserByName(String name);

    BaseResponseDTO createUser(UserDTO dto);

    List<UserDTO> referUserInfo(int page, int limit, String name);

    List<MyRole> referRoleList();

    boolean updateUser(UserDTO dto);

    boolean deleteUser(Long id);
}
