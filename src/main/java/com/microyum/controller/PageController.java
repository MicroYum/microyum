package com.microyum.controller;

import com.microyum.common.util.DateUtils;
import com.microyum.common.util.UserUtils;
import com.microyum.model.MyBlog;
import com.microyum.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "")
public class PageController {

    @Autowired
    private BlogService blogService;

    @RequestMapping
    public ModelAndView mainPage() {
        return new ModelAndView("index");
    }

    @RequestMapping(value = "/index")
    public ModelAndView indexPage() {
        return new ModelAndView("index");
    }

    @RequestMapping(value = "/wealth")
    public ModelAndView wealthPage() {
        return new ModelAndView("wealth");
    }

    @RequestMapping(value = "/message")
    public ModelAndView messagePage() {
        return new ModelAndView("message");
    }

    @RequestMapping(value = "/album")
    public ModelAndView albumPage() {
        return new ModelAndView("album");
    }

    @RequestMapping(value = "/album/{id}/detail")
    public ModelAndView albumDetailPage(@PathVariable("id") Long id) {
        ModelAndView mv = new ModelAndView("album_detail");
        return mv;
    }

    @RequestMapping(value = "/about")
    public ModelAndView aboutPage() {
        return new ModelAndView("about");
    }

    @RequestMapping(value = "/management/main")
    public ModelAndView managePage() {
        return new ModelAndView("management/main");
    }

    @RequestMapping(value = "/management/login")
    public ModelAndView loginPage() {
        return new ModelAndView("management/login");
    }

    @RequestMapping(value = "/management/blog")
    public ModelAndView blogPage() {
        return new ModelAndView("management/blog");
    }

    @RequestMapping(value = "/management/blog/list")
    public ModelAndView blogList() {
        return new ModelAndView("management/blog_list");
    }

    @RequestMapping(value = "/management/stock/base")
    public ModelAndView stockBase() {
        return new ModelAndView("management/stock_base");
    }

    @RequestMapping(value = "/management/exit")
    public ModelAndView managementExit() {
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(null);
        SecurityContextHolder.clearContext();
        return new ModelAndView("management/login");
    }

    @RequestMapping(value = "/blog/{id}/detail")
    public ModelAndView blogDetailPage(@PathVariable("id") Long id, HttpServletRequest request) {

        ModelAndView mv = new ModelAndView("blog_detail");
        MyBlog myBlog = blogService.findBlogDetailById(id, UserUtils.getIpAddress(request), request.getRequestURI());
        if (myBlog != null) {
            mv.addObject("blogId", id);
            mv.addObject("title", myBlog.getTitle());
            mv.addObject("content", myBlog.getContent());
            mv.addObject("createTime", DateUtils.formatDate(myBlog.getCreateTime(), DateUtils.DATE_TIME_FORMAT));

            mv.addObject("blogType", blogService.findArticleTypeById(myBlog.getArticleId()).getName());
        }

        return mv;
    }
}
