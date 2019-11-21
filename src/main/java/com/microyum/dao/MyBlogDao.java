package com.microyum.dao;

import com.microyum.common.util.DateUtils;
import com.microyum.dto.BlogListDTO;
import com.microyum.model.MyBlog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class MyBlogDao {

    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Integer activeBlogTotal(int article) {

        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if (article != 5) {
            parameters.addValue("article", article);
            return namedParameterJdbcTemplate.queryForObject("select count(1) total from `my_blog` where `status` in (1, 3) and `article_id` = :article", parameters, Integer.class);
        } else {
            return namedParameterJdbcTemplate.queryForObject("select count(1) total from `my_blog` where `status` in (1, 3) ", parameters, Integer.class);
        }
    }

    public Long countAllBlog(int article, String title) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        StringBuilder builder = new StringBuilder();
        builder.append(" select count(1) count");
        builder.append(" from my_blog ");
        builder.append(" where 1 = 1");

        if (article > 0) {
            builder.append(" and article_id = :articleId ");
            parameters.addValue("articleId", article);
        }

        if (StringUtils.isNotBlank(title)) {
            builder.append(" and title like :title ");
            parameters.addValue("title", title);
        }

        return  namedParameterJdbcTemplate.queryForObject(builder.toString(), parameters, Long.class);
    }

    public List<BlogListDTO> findAllBlog(int pageNo, int pageSize, int article, String title) {
        int start = (pageNo - 1) * pageSize;

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("start", start);
        parameters.addValue("pageSize", pageSize);

        StringBuilder builder = new StringBuilder();
        builder.append(" select ");
        builder.append("    t1.id, t1.title, t2.name article, ");
        builder.append("    case t1.status ");
        builder.append("    when 0 then '删除' ");
        builder.append("    when 1 then '正常' ");
        builder.append("    when 2 then '置顶' ");
        builder.append("    when 3 then '草稿' ");
        builder.append("    end status, ");
        builder.append("    t1.page_view, t1.create_time ");
        builder.append(" from ");
        builder.append("    my_blog t1 ");
        builder.append(" left join my_article_type t2 on t1.article_id = t2.id");
        builder.append(" where 1 = 1");

        if (article > 0) {
            builder.append(" and t1.article_id = :articleId ");
            parameters.addValue("articleId", article);
        }

        if (StringUtils.isNotBlank(title)) {
            builder.append(" and t1.title like :title ");
            parameters.addValue("title", "%" + title + "%");
        }

        builder.append(" order by ");
        builder.append("    create_time desc ");
        builder.append("    limit :start, :pageSize ");

        List<BlogListDTO> listBlog = namedParameterJdbcTemplate.query(builder.toString(), parameters, (rs, rowNum) -> {
            BlogListDTO blogDTO = new BlogListDTO();
            blogDTO.setId(rs.getLong("id"));
            blogDTO.setTitle(rs.getString("title"));
            blogDTO.setArticle(rs.getString("article"));
            blogDTO.setStatus(rs.getString("status"));
            blogDTO.setPageView(rs.getLong("page_view"));
            blogDTO.setCreateTime(DateUtils.formatDate(rs.getTimestamp("create_time"), DateUtils.DATE_TIME_FORMAT));
            return blogDTO;
        });

        return listBlog;
    }

    public List<MyBlog> findActiveBlogList(int pageNo, int pageSize, int article, String title, boolean isActive) {

        int start = (pageNo - 1) * pageSize;

        Object[] param = new Object[]{start, pageSize};

        StringBuilder builder = new StringBuilder();
        builder.append(" select ");
        builder.append("    id, title, summary, topic_img, article_id, create_time ");
        builder.append(" from ");
        builder.append("    my_blog ");
        builder.append(" where 1 = 1");

        if (isActive) {
            builder.append(" and status in (1, 3) ");
        }

        if (StringUtils.isNotBlank(title)) {
            builder.append(" and title like '%");
            builder.append(title);
            builder.append("%'");
        }

        // article = 5表示罗列所有的博文，根据日期倒序排序
        if (article != 5) {
            builder.append(" and article_id = ? ");
            param = new Object[]{article, start, pageSize};
        }

        builder.append(" order by ");
        builder.append("    create_time desc ");
        builder.append("    limit ?, ? ");

        List<MyBlog> listBlog = jdbcTemplate.query(builder.toString(), param, (rs, rowNum) -> {
            MyBlog myBlog = new MyBlog();
            myBlog.setId(rs.getLong("id"));
            myBlog.setTitle(rs.getString("title"));
            myBlog.setSummary(rs.getString("summary"));
            myBlog.setTopicImg(rs.getString("topic_img"));
            myBlog.setArticleId(rs.getLong("article_id"));
            myBlog.setCreateTime(rs.getTimestamp("create_time"));
            return myBlog;
        });

        return listBlog;
    }

    public MyBlog findBlogDetailById(Long id) {

        List<MyBlog> listBlog = jdbcTemplate.query("select * from my_blog where id = ?", new Object[]{id}, (rs, rowNum) -> getMyBlog(rs));
        return listBlog == null ? null : listBlog.get(0);
    }

    public int save(MyBlog blog) {
        return jdbcTemplate.update("insert into my_blog(user_id, title, topic_img, summary, content, status, article_id, page_view) values (?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{
                blog.getUserId(), blog.getTitle(), blog.getTopicImg(), blog.getSummary(), blog.getContent(), blog.getStatus(), blog.getArticleId(), blog.getPageView()
        });
    }

    public int updatePageView(Long id, Long pageView) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", id);
        parameters.addValue("pageView", pageView);
        return namedParameterJdbcTemplate.update("update `my_blog` set `page_view` = :pageView where `id` = :id", parameters);
    }

    private MyBlog getMyBlog(ResultSet rs) throws SQLException {
        MyBlog myBlog = new MyBlog();
        myBlog.setId(rs.getLong("id"));
        myBlog.setUserId(rs.getLong("user_id"));
        myBlog.setTitle(rs.getString("title"));
        myBlog.setTopicImg(rs.getString("topic_img"));
        myBlog.setSummary(rs.getString("summary"));
        myBlog.setContent(rs.getString("content"));
        myBlog.setStatus(rs.getByte("status"));
        myBlog.setArticleId(rs.getLong("article_id"));
        myBlog.setPageView(rs.getLong("page_view"));
        myBlog.setCreateTime(rs.getTimestamp("create_time"));
        myBlog.setLastUpdateTime(rs.getTimestamp("last_update_time"));

        return myBlog;
    }
}
