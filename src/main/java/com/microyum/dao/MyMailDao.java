package com.microyum.dao;

import com.microyum.model.MyMailTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class MyMailDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public MyMailTemplate findTemplateByName(String name) {
        String sql = "select * from my_mail_template where name = ?";
        List<MyMailTemplate> listMail = jdbcTemplate.query(sql, new Object[]{name}, (rs, rowNum) -> {
            MyMailTemplate template = new MyMailTemplate();
            template.setMailBody(rs.getString("mail_body"));
            return template;
        });

        if (listMail.size() != 1) {
            return null;
        }

        return listMail.get(0);
    }

}
