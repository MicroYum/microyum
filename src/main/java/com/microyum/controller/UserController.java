package com.microyum.controller;

import com.google.common.collect.Lists;
import com.microyum.common.http.BaseResponseDTO;
import com.microyum.common.http.HttpStatus;
import com.microyum.common.util.CaptchaUtils;
import com.microyum.common.util.DateUtils;
import com.microyum.common.util.UserUtils;
import com.microyum.dto.UserDto;
import com.microyum.model.common.MyUser;
import com.microyum.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author syaka.hong
 */
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/user/captcha")
    @ResponseBody
    public String captcha(HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");

        OutputStream os = response.getOutputStream();
        //返回验证码和图片的map
        Map<String, Object> map = CaptchaUtils.getImageCode(80, 25, os);
        request.getSession().setAttribute("captcha", map.get("strEnsure").toString().toLowerCase());
        request.getSession().setAttribute("captchaTime", DateUtils.getCurrentDateTime());
        try {
            ImageIO.write((BufferedImage) map.get("image"), "jpg", os);
        } catch (IOException e) {
            return "";
        } finally {
            if (os != null) {
                os.flush();
                os.close();
            }
        }
        return null;
    }

    @GetMapping(value = "/user/info", produces = "application/json")
    public BaseResponseDTO getUserInfo(String name) {

        MyUser myUser = userService.getUserByName(name);
        if (myUser == null) {
            return new BaseResponseDTO(HttpStatus.DATA_NOT_FOUND);
        }

        return new BaseResponseDTO(HttpStatus.OK, myUser);
    }

    @PostMapping(value = "/user/create")
    public BaseResponseDTO createUser(UserDto userDTO) {

        return userService.createUser(userDTO);
    }

    @PostMapping(value = "/user/update")
    public BaseResponseDTO updateUser(UserDto userDTO) {

        boolean result = userService.updateUser(userDTO);

        if (result) {
            return new BaseResponseDTO(HttpStatus.OK);
        }

        return new BaseResponseDTO(HttpStatus.ERROR_IN_DATABASE);
    }

    @GetMapping(value = "/user/{id}/delete")
    public BaseResponseDTO deleteUser(@PathVariable("id") Long id) {

        boolean result = userService.deleteUser(id);

        if (result) {
            return new BaseResponseDTO(HttpStatus.OK);
        }

        return new BaseResponseDTO(HttpStatus.ERROR_IN_DATABASE);
    }

    @GetMapping(value = "/user/list/overview", produces = "application/json")
    public BaseResponseDTO userListOverview(int page, int limit, String name) {

        MyUser user = UserUtils.currentUser();

        return userService.userListOverview(page, limit, user.getId(), name);
    }

    @GetMapping(value = "/user/list", produces = "application/json")
    public BaseResponseDTO userList() {

        MyUser user = UserUtils.currentUser();
        List<MyUser> userList = userService.userList(user.getId());
        if (userList == null) {
            userList = Lists.newArrayList();
            userList.add(user);
        }

        return new BaseResponseDTO(HttpStatus.OK, userList);
    }

    @GetMapping(value = "/refer/role/list", produces = "application/json")
    public BaseResponseDTO referRoleList() {
        return new BaseResponseDTO(HttpStatus.OK, userService.referRoleList());
    }
}
