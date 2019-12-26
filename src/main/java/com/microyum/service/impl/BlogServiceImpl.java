package com.microyum.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.microyum.common.Constants;
import com.microyum.common.http.BaseResponseDTO;
import com.microyum.common.http.HttpStatus;
import com.microyum.common.util.DateUtils;
import com.microyum.dao.jpa.MyArticleTypeDao;
import com.microyum.dao.jdbc.MyBlogJdbcDao;
import com.microyum.dao.jpa.MyBlogLogDao;
import com.microyum.dto.BlogDetailPaging;
import com.microyum.dto.BlogListDto;
import com.microyum.dto.BlogRequestDto;
import com.microyum.model.common.MyArticleType;
import com.microyum.model.blog.MyBlog;
import com.microyum.model.blog.MyBlogLog;
import com.microyum.service.BlogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class BlogServiceImpl implements BlogService {

    @Autowired
    private MyBlogJdbcDao blogJdbcDao;
    @Autowired
    private MyBlogLogDao blogLogDao;
    @Autowired
    private MyArticleTypeDao articleTypeDao;

    @Override
    public BaseResponseDTO listActiveBlog(int pageNo, int pageSize, int article) {

        Map<String, Object> result = Maps.newHashMap();
        List<MyBlog> listBlog = blogJdbcDao.findActiveBlogList(pageNo, pageSize, article, "", true);
        Integer total = blogJdbcDao.activeBlogTotal(article);

        List<String> list = Lists.newArrayList();
        for (MyBlog myBlog : listBlog) {
            String template = Constants.BLOG_LIST_SUMMARY;

            // 最近5天发布的文章给予New_Button标志
            if (DateUtils.getDateDiff(myBlog.getCreateTime(), new Date(), TimeUnit.DAYS) <= 5) {
                template = template.replace("{#newButton#}", Constants.BLOG_NEW_BUTTON);
            } else {
                template = template.replace("{#newButton#}", "");
            }

            template = template.replace("{#title#}", myBlog.getTitle());
            if (StringUtils.isBlank(myBlog.getTopicImg())) {
                template = template.replace("{#topicImg#}", Constants.BLOG_NO_PICTURE);
            } else {
                template = template.replace("{#topicImg#}", myBlog.getTopicImg());
            }
            template = template.replace("{#summary#}", myBlog.getSummary());
            template = template.replace("{#blodId#}", String.valueOf(myBlog.getId()));
            template = template.replace("{#createTime#}", DateUtils.formatDate(myBlog.getCreateTime(), DateUtils.DATE_TIME_FORMAT));
            list.add(template);
        }

        result.put("list", list);
        result.put("total", total == 0 ? 1 : total);

        return new BaseResponseDTO(HttpStatus.OK, result);
    }

    @Override
    public BaseResponseDTO listAllBlog(int pageNo, int pageSize, int article, String title) {

        List<BlogListDto> listBlog = blogJdbcDao.findAllBlog(pageNo, pageSize, article, title);

        BaseResponseDTO responseDTO = new BaseResponseDTO(HttpStatus.OK_LAYUI, listBlog);
        responseDTO.setCount(blogJdbcDao.countAllBlog(article, title));

        return responseDTO;
    }

    @Override
    public BaseResponseDTO findAllArticleType() {
        List<MyArticleType> list = articleTypeDao.findAllArticleType();
        return new BaseResponseDTO(HttpStatus.OK, list);
    }

    @Override
    public MyArticleType findArticleTypeById(Long id) {

        Optional<MyArticleType> articleType = articleTypeDao.findById(id);
        return articleType.get();
    }


    @Override
    @Transactional
    public MyBlog findBlogDetailById(Long id, String ip, String contextPath) {

        MyBlog blog = blogJdbcDao.findBlogDetailById(id);
        blogJdbcDao.updatePageView(id, blog.getPageView() + 1);

        MyBlogLog blogLog = new MyBlogLog();
        blogLog.setIpAddr(ip);
        blogLog.setRequestPath(contextPath);
        blogLogDao.save(blogLog);

        return blog;
    }

    @Override
    public BaseResponseDTO findBlogDetailPaging(Long id, BlogDetailPaging blogDetailPaging, String ip, String contextPath) {

        MyBlog blog;
        MyArticleType articleType = articleTypeDao.findByName(blogDetailPaging.getArticle());
        if (StringUtils.equals(blogDetailPaging.getKbn(), "pre")) {
            blog = blogJdbcDao.findPostBlogDetail(id, articleType.getId());
        } else {
            blog = blogJdbcDao.findPreBlogDetail(id, articleType.getId());
        }

        return new BaseResponseDTO(HttpStatus.OK, blog);
    }

    @Override
    public BaseResponseDTO saveBlog(BlogRequestDto blogDto) {

        MyBlog entity = new MyBlog();
        BeanUtils.copyProperties(blogDto, entity);
        entity.setUserId(1L);
        entity.setPageView(0L);

        if (StringUtils.isBlank(entity.getTopicImg())) {
            entity.setTopicImg(Constants.BLOG_NO_PICTURE);
        }

        try {
            blogJdbcDao.save(entity);
        } catch (Exception e) {
            log.error("Save blog error.", e);
            return new BaseResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new BaseResponseDTO(HttpStatus.OK);
    }


}
