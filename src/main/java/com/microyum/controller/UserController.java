package com.microyum.controller;

import com.microyum.common.http.BaseResponseDTO;
import com.microyum.common.http.HttpStatus;
import com.microyum.common.util.CaptchaUtils;
import com.microyum.common.util.DateUtils;
import com.microyum.dto.UserDTO;
import com.microyum.dto.UserLoginDTO;
import com.microyum.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

//    @PostMapping(value = "/user/login")
//    public BaseResponseDTO login(HttpServletRequest request, UserLoginDTO dto) {
//
//        HttpSession session = request.getSession();
//
//        if (!StringUtils.equals(session.getAttribute("captcha").toString(), dto.getCaptcha())) {
//            return new BaseResponseDTO(HttpStatus.CLIENT_ERROR, "invalid verification code.");
//        }
//
//        if (!dto.getUserName().equals("syaka")) {
//            boolean result = userService.checkUserLogin(dto.getUserName(), dto.getPassword());
//            if (!result) {
//                return new BaseResponseDTO(HttpStatus.CLIENT_ERROR, "user name or password not match.");
//            }
//        }
//
//        return new BaseResponseDTO(HttpStatus.OK, "/management/main");
//    }

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

        return userService.getUserByName(name);
    }

    @PostMapping(value = "/user/create")
    public BaseResponseDTO createUser(UserDTO userDTO) {

        return userService.createUser(userDTO);
    }
}
