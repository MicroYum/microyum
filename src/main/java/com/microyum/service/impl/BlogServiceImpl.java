package com.microyum.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.microyum.common.Constants;
import com.microyum.common.http.BaseResponseDTO;
import com.microyum.common.http.HttpStatus;
import com.microyum.common.util.DateUtils;
import com.microyum.dao.MyArticleTypeDao;
import com.microyum.dao.MyBlogDao;
import com.microyum.dao.MyBlogLogDao;
import com.microyum.dto.BlogDetailPaging;
import com.microyum.dto.BlogListDTO;
import com.microyum.dto.BlogRequestDTO;
import com.microyum.model.MyArticleType;
import com.microyum.model.MyBlog;
import com.microyum.model.MyBlogLog;
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
    private MyBlogDao myBlogDao;
    @Autowired
    private MyBlogLogDao myBlogLogDao;
    @Autowired
    private MyArticleTypeDao myArticleTypeDao;

    @Override
    public BaseResponseDTO listActiveBlog(int pageNo, int pageSize, int article) {

        Map<String, Object> result = Maps.newHashMap();
        List<MyBlog> listBlog = myBlogDao.findActiveBlogList(pageNo, pageSize, article, "", true);
        Integer total = myBlogDao.activeBlogTotal(article);

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

        List<BlogListDTO> listBlog = myBlogDao.findAllBlog(pageNo, pageSize, article, title);

        BaseResponseDTO responseDTO = new BaseResponseDTO(HttpStatus.OK_LAYUI, listBlog);
        responseDTO.setCount(myBlogDao.countAllBlog(article, title));

        return responseDTO;
    }

    @Override
    public BaseResponseDTO findAllArticleType() {
        List<MyArticleType> list = myArticleTypeDao.findAllArticleType();
        return new BaseResponseDTO(HttpStatus.OK, list);
    }

    @Override
    public MyArticleType findArticleTypeById(Long id) {

        Optional<MyArticleType> articleType = myArticleTypeDao.findById(id);
        return articleType.get();
    }


    @Override
    @Transactional
    public MyBlog findBlogDetailById(Long id, String ip, String contextPath) {

        MyBlog blog = myBlogDao.findBlogDetailById(id);
        myBlogDao.updatePageView(id, blog.getPageView() + 1);

        MyBlogLog blogLog = new MyBlogLog();
        blogLog.setIpAddr(ip);
        blogLog.setRequestPath(contextPath);
        myBlogLogDao.save(blogLog);

        return blog;
    }

    @Override
    public BaseResponseDTO findBlogDetailPaging(Long id, BlogDetailPaging blogDetailPaging, String ip, String contextPath) {

        MyBlog blog;
        MyArticleType articleType = myArticleTypeDao.findByName(blogDetailPaging.getArticle());
        if (StringUtils.equals(blogDetailPaging.getKbn(), "pre")) {
            blog = myBlogDao.findPostBlogDetail(id, articleType.getId());
        } else {
            blog = myBlogDao.findPreBlogDetail(id, articleType.getId());
        }

        return new BaseResponseDTO(HttpStatus.OK, blog);
    }

    @Override
    public BaseResponseDTO saveBlog(BlogRequestDTO blogDto) {

        MyBlog entity = new MyBlog();
        BeanUtils.copyProperties(blogDto, entity);
        entity.setUserId(1L);
        entity.setPageView(0L);

        if (StringUtils.isBlank(entity.getTopicImg())) {
            entity.setTopicImg(Constants.BLOG_NO_PICTURE);
        }

        try {
            myBlogDao.save(entity);
        } catch (Exception e) {
            log.error("Save blog error.", e);
            return new BaseResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new BaseResponseDTO(HttpStatus.OK);
    }


}
