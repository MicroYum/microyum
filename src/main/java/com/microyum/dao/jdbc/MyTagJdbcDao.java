package com.microyum.dao.jdbc;

import com.microyum.common.util.DateUtils;
import com.microyum.common.util.StringUtils;
import com.microyum.dto.TagDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MyTagJdbcDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;

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
            tagDto.setCategory(rs.getString("category"));
            tagDto.setItems(rs.getLong("items"));
            tagDto.setLastUpdateTime(DateUtils.formatDate(rs.getTimestamp("last_update_time"), DateUtils.DATE_TIME_FORMAT));
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

    public List<String> findEntityNameByTagId(Long id) {

        String sql = "select b.stock_name from my_tag t, my_tag_binding tb, my_stock_base b " +
                "where t.id = tb.tag_id and tb.entity_id = b.id and t.id = ?";

        return jdbcTemplate.queryForList(sql, new Object[]{id}, String.class);
    }

    private String findTagInfoSql(String name, boolean isCount) {
        StringBuilder builder = new StringBuilder();

        builder.append("select ");

        if (isCount) {
            builder.append(" count(1) ");
        } else {
            builder.append(" id, name, category, items, last_update_time");
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
