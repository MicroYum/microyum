package com.microyum.service;

import com.microyum.common.http.BaseResponseDTO;
import com.microyum.dto.UserDTO;

public interface UserService {

    boolean checkUserLogin(String userName, String password);

    BaseResponseDTO getUserByName(String name);

    BaseResponseDTO createUser(UserDTO dto);
}
