package com.microyum.controller;

import com.google.common.collect.Maps;
import com.microyum.common.http.BaseResponseDTO;
import com.microyum.common.http.HttpStatus;
import com.microyum.common.util.UserUtils;
import com.microyum.dto.BlogDetailPaging;
import com.microyum.dto.BlogRequestDTO;
import com.microyum.model.MyBlog;
import com.microyum.service.BlogService;
import com.microyum.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class BlogController {

    @Autowired
    private BlogService blogService;
    @Autowired
    private FileService fileService;

    @RequestMapping(value = "/public/list/all/blog", produces = "application/json")
    public BaseResponseDTO listAllBlog(HttpServletRequest request) {

        int pageNo = Integer.parseInt(request.getParameter("page"));
        int pageSize = Integer.parseInt(request.getParameter("limit"));;
        int article = Integer.parseInt(request.getParameter("article"));;
        String title = request.getParameter("selectTitle");
        return blogService.listAllBlog(pageNo, pageSize, article, title);
    }


    @RequestMapping(value = "/public/list/active/blog", produces = "application/json")
    public BaseResponseDTO listActiveBlog(int pageNo, int pageSize, int article) {
        return blogService.listActiveBlog(pageNo, pageSize, article);
    }

    @RequestMapping(value = "/public/list/article/type", produces = "application/json")
    public BaseResponseDTO listArticleType() {
        return blogService.findAllArticleType();
    }


    @RequestMapping(value = "/public/blog/paging/{id}", produces = "application/json")
    public BaseResponseDTO searchBlog(@PathVariable("id") Long id, BlogDetailPaging paging, HttpServletRequest request) {

        BaseResponseDTO responseDTO = blogService.findBlogDetailPaging(id, paging, UserUtils.getIpAddress(request), request.getRequestURI());

        return responseDTO;
    }

    @RequestMapping(value = "/save/blog", produces = "application/json")
    public BaseResponseDTO saveBlog(BlogRequestDTO blogDto) {
        return blogService.saveBlog(blogDto);
    }

    @RequestMapping(value = "/edit/blog", produces = "application/json")
    public BaseResponseDTO editBlog() {

        return null;
    }

    @RequestMapping(value = "/delete/blog", produces = "application/json")
    public BaseResponseDTO deleteBlog() {

        return null;
    }

    @RequestMapping(value = "/upload/blog/picture")
    @ResponseBody
    public Map<String, Object> uploadPicture(@RequestParam("imgFile") MultipartFile imgFile) {

        Map<String, Object> result = Maps.newHashMap();

        String picturePath = fileService.saveBlogPicture(imgFile);
        if (picturePath == null) {
            result.put("error", 1);
            result.put("message", "保存图片失败");
        } else {
            result.put("error", 0);
            result.put("url", picturePath);
        }

        return result;
    }

    @RequestMapping(value = "/save/article/type", produces = "application/json")
    public BaseResponseDTO saveArticleType(String type) {

        return null;
    }
}
