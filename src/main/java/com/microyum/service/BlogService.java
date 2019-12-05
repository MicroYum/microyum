package com.microyum.service;

import com.microyum.common.http.BaseResponseDTO;
import com.microyum.dto.BlogDetailPaging;
import com.microyum.dto.BlogRequestDTO;
import com.microyum.model.MyArticleType;
import com.microyum.model.MyBlog;

public interface BlogService {

    BaseResponseDTO saveBlog(BlogRequestDTO blogDto);

    BaseResponseDTO listActiveBlog(int pageNo, int pageSize, int article);

    BaseResponseDTO listAllBlog(int pageNo, int pageSize, int article, String title);

    BaseResponseDTO findAllArticleType();

    MyArticleType findArticleTypeById(Long id);

    MyBlog findBlogDetailById(Long id, String ip, String contextPath);

    BaseResponseDTO findBlogDetailPaging(Long id, BlogDetailPaging blogDetailPaging, String ip, String contextPath);
}
