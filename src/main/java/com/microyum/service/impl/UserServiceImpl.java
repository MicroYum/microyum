package com.microyum.service.impl;

import com.microyum.common.http.BaseResponseDTO;
import com.microyum.common.http.HttpStatus;
import com.microyum.common.util.StringUtils;
import com.microyum.dao.MyUserDao;
import com.microyum.dto.UserDTO;
import com.microyum.model.MyUser;
import com.microyum.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private MyUserDao myUserDao;

    @Override
    public boolean checkUserLogin(String userName, String password) {

        MyUser myUser = myUserDao.findByNameAndPassword(userName, StringUtils.md5EncryptSlat(password));
        if (myUser != null) {
            return true;
        }

        return false;
    }

    @Override
    public BaseResponseDTO getUserByName(String userName) {

        MyUser myUser = myUserDao.findByName(userName);

        if (myUser == null) {
            return new BaseResponseDTO(HttpStatus.DATA_NOT_FOUND);
        }

        return new BaseResponseDTO(HttpStatus.OK, myUser);
    }

    @Override
    public BaseResponseDTO createUser(UserDTO dto) {

        try {

            MyUser user = myUserDao.findByName(dto.getName());
            if (user != null) {
                return new BaseResponseDTO(HttpStatus.DATA_SHOULD_NOT_EXIST, "user name not unique.");
            }

            MyUser entity = new MyUser();
            BeanUtils.copyProperties(dto, entity);
            entity.setPassword(StringUtils.md5EncryptSlat(dto.getPassword()));
            myUserDao.save(entity);
        } catch (Exception e) {

            return new BaseResponseDTO(HttpStatus.ERROR_IN_DATABASE);
        }

        return new BaseResponseDTO(HttpStatus.OK);
    }
}
