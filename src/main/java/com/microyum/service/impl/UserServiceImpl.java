package com.microyum.service.impl;

import com.microyum.common.Constants;
import com.microyum.common.http.BaseResponseDTO;
import com.microyum.common.http.HttpStatus;
import com.microyum.common.util.StringUtils;
import com.microyum.dao.jpa.MyRoleDao;
import com.microyum.dao.jpa.MyUserDao;
import com.microyum.dao.jdbc.MyUserJdbcDao;
import com.microyum.dao.jpa.MyUserRoleDao;
import com.microyum.dto.UserDto;
import com.microyum.model.common.MyRole;
import com.microyum.model.common.MyUser;
import com.microyum.model.common.MyUserRole;
import com.microyum.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.apache.bcel.generic.RET;
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
    private MyUserDao userDao;
    @Autowired
    private MyRoleDao roleDao;
    @Autowired
    private MyUserRoleDao userRoleDao;
    @Autowired
    private MyUserJdbcDao userJdbcDao;

    @Override
    public boolean checkUserLogin(String userName, String password) {

        MyUser myUser = userDao.findByName(userName);

        myUser = userDao.findByNameAndPassword(userName, StringUtils.md5EncryptSlat(password, myUser.getSalt()));
        if (myUser != null) {
            return true;
        }

        return false;
    }

    @Override
    public MyUser getUserByName(String userName) {

        return userDao.findByName(userName);
    }

    @Override
    @Transactional
    public BaseResponseDTO createUser(UserDto dto) {

        try {

            MyUser user = userDao.findByName(dto.getName());
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
            userDao.save(entity);

            MyUser myUser = userDao.findByName(entity.getName());
            MyUserRole userRole = new MyUserRole();
            userRole.setUserId(myUser.getId());
            userRole.setRoleId(dto.getRoleId());
            userRoleDao.save(userRole);
        } catch (Exception e) {

            return new BaseResponseDTO(HttpStatus.ERROR_IN_DATABASE);
        }

        return new BaseResponseDTO(HttpStatus.OK);
    }

    @Override
    public BaseResponseDTO userListOverview(int page, int limit, long userId, String name) {

        String roleName = roleDao.findRoleNameByUserId(userId);

        int start = (page - 1) * limit;
        long count;
        List<UserDto> userList;
        if (StringUtils.isNotBlank(name)) {

            if (StringUtils.equals(roleName, Constants.USER_ROLE_ADMIN)) {
                userList = userJdbcDao.findByNameOrNickName(start, limit, name);
                count = userJdbcDao.findByNameOrNickNameCount(name);
            } else {
                userList = userJdbcDao.findByNameOrNickName(start, limit, userId, name);
                count = userJdbcDao.findByNameOrNickNameCount(name, userId);
            }
        } else {

            if (StringUtils.equals(roleName, Constants.USER_ROLE_ADMIN)) {
                userList = userJdbcDao.findUserInfoPaging(start, limit, userId);
                count = userJdbcDao.findUserInfoCount(userId);
            } else {
                userList = userJdbcDao.findUserInfoPaging(start, limit);
                count = userJdbcDao.findUserInfoCount();
            }
        }

        BaseResponseDTO responseDTO = new BaseResponseDTO(HttpStatus.OK_LAYUI, userList);
        responseDTO.setCount(count);

        return responseDTO;
    }

    @Override
    public List<MyRole> referRoleList() {
        return roleDao.findAll();
    }

    @Override
    @Transactional
    public boolean updateUser(UserDto dto) {

        try {
            MyUser myUser = userDao.findByUId(dto.getId());
            myUser.setName(dto.getName());
            myUser.setNickName(dto.getNickName());
            myUser.setTelephone(dto.getTelephone());
            myUser.setEmail(dto.getEmail());
            userDao.save(myUser);

            MyUserRole userRole = userRoleDao.findByUserId(dto.getId());
            userRole.setRoleId(dto.getRoleId());
            userRoleDao.save(userRole);
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
            userDao.deleteById(id);
            userRoleDao.deleteByUserId(id);
        } catch (Exception e) {
            log.error("Delete User Error, ", e);
            return false;
        }

        return true;
    }

    @Override
    public List<MyUser> userList(Long userId) {

        String roleName = roleDao.findRoleNameByUserId(userId);

        if (StringUtils.equals(Constants.USER_ROLE_ADMIN, roleName)) {
            return userDao.findAll();
        }

        return null;
    }

    @Override
    public String referUserRoleName(Long userId) {

        return roleDao.findRoleNameByUserId(userId);
    }
}
