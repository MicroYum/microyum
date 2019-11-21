package com.microyum.dao;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Map;

@Repository
public class MyAlbumDao {

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Long findIdBySummary(String summary) {
        String sql = "select id from my_album where summary = :summary";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("summary", summary);
        return namedParameterJdbcTemplate.queryForObject(sql, parameters, Long.class);
    }

    public int save(Map<String, Object> param) {
        String sql = "";
        return namedParameterJdbcTemplate.update(sql, param);
    }
}
