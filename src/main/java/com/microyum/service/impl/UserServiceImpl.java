package com.microyum.service.impl;

import com.microyum.common.http.BaseResponseDTO;
import com.microyum.common.http.HttpStatus;
import com.microyum.common.util.StringUtils;
import com.microyum.dao.MyRoleDao;
import com.microyum.dao.MyUserDao;
import com.microyum.dao.MyUserJdbcDao;
import com.microyum.dao.MyUserRoleDao;
import com.microyum.dto.UserDto;
import com.microyum.model.MyRole;
import com.microyum.model.MyUser;
import com.microyum.model.MyUserRole;
import com.microyum.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private MyUserDao myUserDao;
    @Autowired
    private MyRoleDao myRoleDao;
    @Autowired
    private MyUserRoleDao myUserRoleDao;
    @Autowired
    private MyUserJdbcDao myUserJdbcDao;

    @Override
    public boolean checkUserLogin(String userName, String password) {

        MyUser myUser = myUserDao.findByName(userName);

        myUser = myUserDao.findByNameAndPassword(userName, StringUtils.md5EncryptSlat(password, myUser.getSalt()));
        if (myUser != null) {
            return true;
        }

        return false;
    }

    @Override
    public MyUser getUserByName(String userName) {

        MyUser myUser = myUserDao.findByName(userName);

        if (myUser == null) {
            return null;
        }

        return myUser;
    }

    @Override
    @Transactional
    public BaseResponseDTO createUser(UserDto dto) {

        try {

            MyUser user = myUserDao.findByName(dto.getName());
            if (user != null) {
                return new BaseResponseDTO(HttpStatus.DATA_SHOULD_NOT_EXIST, "user name not unique.");
            }

            String salt = UUID.randomUUID().toString().replace("-", "");

            MyUser entity = new MyUser();
            BeanUtils.copyProperties(dto, entity);
            entity.setPassword(StringUtils.md5EncryptSlat(dto.getPassword(), salt));
            entity.setSalt(salt);
            entity.setLocked(false);
            if (dto.getParentId() != null) {
                entity.setParentId(Long.valueOf(dto.getParentId()));
            }
            myUserDao.save(entity);

            MyUser myUser = myUserDao.findByName(entity.getName());
            MyUserRole userRole = new MyUserRole();
            userRole.setUserId(myUser.getId());
            userRole.setRoleId(dto.getRoleId());
            myUserRoleDao.save(userRole);
        } catch (Exception e) {

            return new BaseResponseDTO(HttpStatus.ERROR_IN_DATABASE);
        }

        return new BaseResponseDTO(HttpStatus.OK);
    }

    @Override
    public BaseResponseDTO userListOverview(int page, int limit, String name) {

        int start = (page - 1) * limit;
        long count;
        List<UserDto> userList;
        if (StringUtils.isNotBlank(name)) {
            userList = myUserJdbcDao.findByNameOrNickName(start, limit, name);
            count = myUserJdbcDao.findByNameOrNickNameCount(name);
        } else {
            userList = myUserJdbcDao.findUserInfoPaging(start, limit);
            count = myUserJdbcDao.findUserInfoCount();
        }

        BaseResponseDTO responseDTO = new BaseResponseDTO(HttpStatus.OK_LAYUI, userList);
        responseDTO.setCount(count);

        return responseDTO;
    }

    @Override
    public List<MyRole> referRoleList() {
        return myRoleDao.findAll();
    }

    @Override
    @Transactional
    public boolean updateUser(UserDto dto) {

        try {
            MyUser myUser = myUserDao.findByUId(dto.getId());
            myUser.setName(dto.getName());
            myUser.setNickName(dto.getNickName());
            myUser.setTelephone(dto.getTelephone());
            myUser.setEmail(dto.getEmail());
            myUserDao.save(myUser);

            MyUserRole userRole = myUserRoleDao.findByUserId(dto.getId());
            userRole.setRoleId(dto.getRoleId());
            myUserRoleDao.save(userRole);
        } catch (Exception e) {
            log.error("Update User Error, ", e);
            return false;
        }

        return true;
    }

    @Override
    @Transactional
    public boolean deleteUser(Long id) {

        try {
            myUserDao.deleteById(id);
            myUserRoleDao.deleteByUserId(id);
        } catch (Exception e) {
            log.error("Delete User Error, ", e);
            return false;
        }

        return true;
    }

    @Override
    public List<MyUser> userList() {
        return myUserDao.findAll();
    }
}
