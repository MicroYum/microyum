package com.microyum.dao.jdbc;

import com.microyum.common.util.StringUtils;
import com.microyum.dto.TagDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MyTagJdbcDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<TagDto> findByTagNamePaging(int start, int limit) {
        return this.findByTagNamePaging(start, limit, null);
    }

    public List<TagDto> findByTagNamePaging(int start, int limit, String name) {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("start", start);
        parameters.addValue("limit", limit);
        if (StringUtils.isNotBlank(name)) {
            parameters.addValue("name", "%" + name + "%");
        }

        return namedParameterJdbcTemplate.query(this.findTagInfoSql(name, false), parameters, (rs, rowNum) -> {
            TagDto tagDto = new TagDto();
            tagDto.setId(rs.getLong("id"));
            tagDto.setName(rs.getString("name"));
            tagDto.setCategory(rs.getInt("category"));
            tagDto.setItems(rs.getLong("items"));
            return tagDto;
        });
    }

    public Long findByTagNameCount() {
        return this.findByTagNameCount(null);
    }

    public Long findByTagNameCount(String name) {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        if (StringUtils.isNotBlank(name)) {
            parameters.addValue("name", "%" + name + "%");
        }

        return namedParameterJdbcTemplate.queryForObject(this.findTagInfoSql(name, true), parameters, Long.class);
    }

    private String findTagInfoSql(String name, boolean isCount) {
        StringBuilder builder = new StringBuilder();

        if (isCount) {
            builder.append("select count(1) ");
        } else {
            builder.append(" name, category, items");
        }

        builder.append(" from my_tag ");

        if (StringUtils.isNotBlank(name)) {
            builder.append(" where name like :name ");
        }

        if (!isCount) {
            builder.append(" limit :start, :limit ");
        }

        return builder.toString();
    }
}
