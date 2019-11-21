package com.microyum.dao;

import com.google.common.collect.Maps;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Repository
public class MyAlbumDetailDao {

    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Map<String, String>> findAlbumDetailById(Long albumId) {
        String sql = "select `album_id`, `path` from `my_album_detail` where status != 3 and album_id = :albumId order by status desc";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("albumId", albumId);
        return namedParameterJdbcTemplate.query(sql, parameters, (rs, rowNum) -> {
            Map<String, String> map = Maps.newHashMap();
            map.put("albumId", rs.getString("album_id"));
            map.put("path", rs.getString("path"));
            return map;
        });
    }

    public int[] batchSave(Map<String, Object>[] maps) {
        String sql = "insert into `my_album_detail`(`albumId`, `path`, `status`) values (:albumId, :path, :status)";

        return namedParameterJdbcTemplate.batchUpdate(sql, maps);
    }
}
