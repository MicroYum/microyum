package com.microyum.dao.jdbc;

import com.microyum.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MyUserJdbcDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Long findByNameOrNickNameCount(String name, Long userId) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("name", "%" + name + "%");
        if (userId != null) {
            parameters.addValue("userId", userId);
        }
        return namedParameterJdbcTemplate.queryForObject(this.findUserInfoSql(true, true, userId), parameters, Long.class);
    }

    public Long findByNameOrNickNameCount(String name) {
        return this.findByNameOrNickNameCount(name, null);
    }

    public List<UserDto> findByNameOrNickName(int start, int limit, Long userId, String name) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("start", start);
        parameters.addValue("limit", limit);
        parameters.addValue("name", "%" + name + "%");
        if (userId == null) {
            parameters.addValue("userId", userId);
        }

        return namedParameterJdbcTemplate.query(this.findUserInfoSql(true, false, userId), parameters, (rs, rowNum) -> {
            UserDto userDTO = new UserDto();
            userDTO.setId(rs.getLong("id"));
            userDTO.setName(rs.getString("name"));
            userDTO.setNickName(rs.getString("nick_name"));
            userDTO.setEmail(rs.getString("email"));
            userDTO.setTelephone(rs.getString("telephone"));
            userDTO.setLastUpdateDate(rs.getString("last_update_time"));
            userDTO.setRoleId(rs.getLong("role_id"));
            userDTO.setRoleName(rs.getString("role_name"));
            userDTO.setLocked(rs.getString("locked"));
            return userDTO;
        });
    }

    public List<UserDto> findByNameOrNickName(int start, int limit, String name) {

        return this.findByNameOrNickName(start, limit, null, name);
    }

    public Long findUserInfoCount(Long userId) {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        if (userId != null) {
            parameters.addValue("userId", userId);
        }

        return namedParameterJdbcTemplate.queryForObject(this.findUserInfoSql(false, true, userId), parameters, Long.class);
    }

    public Long findUserInfoCount() {

        return this.findUserInfoCount(null);
    }

    public List<UserDto> findUserInfoPaging(int start, int limit, Long userId) {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("start", start);
        parameters.addValue("limit", limit);
        if (userId != null) {
            parameters.addValue("userId", userId);
        }

        return namedParameterJdbcTemplate.query(this.findUserInfoSql(false, false, userId), parameters, (rs, rowNum) -> {
            UserDto userDTO = new UserDto();
            userDTO.setId(rs.getLong("id"));
            userDTO.setName(rs.getString("name"));
            userDTO.setNickName(rs.getString("nick_name"));
            userDTO.setEmail(rs.getString("email"));
            userDTO.setTelephone(rs.getString("telephone"));
            userDTO.setLastUpdateDate(rs.getString("last_update_time"));
            userDTO.setRoleId(rs.getLong("role_id"));
            userDTO.setRoleName(rs.getString("role_name"));
            userDTO.setLocked(rs.getString("locked"));
            return userDTO;
        });
    }

    public List<UserDto> findUserInfoPaging(int start, int limit) {

        return this.findUserInfoPaging(start, limit, null);
    }

    private String findUserInfoSql(boolean hasCondition, boolean isCount, Long userId) {
        StringBuilder builder = new StringBuilder();

        if (isCount) {
            builder.append("select count(1) ");
        } else {
            builder.append("select mu.id as id, mu.name as name, mu.nick_name as nick_name, mu.email as email, ");
            builder.append(" mu.telephone as telephone, DATE_FORMAT(mu.last_update_time,'%Y-%m-%d %H:%i:%s') as last_update_time, ");
            builder.append(" mr.name as role_name, mr.id as role_id, case mu.locked when true then 'Yes' else 'No' end as locked ");
        }

        builder.append(" from my_user mu inner join my_user_role ur on ur.user_id = mu.id ");
        builder.append(" inner join my_role mr on mr.id = ur.role_id ");
        if (hasCondition) {
            builder.append(" where mu.name like :name or mu.nick_name like :name ");

            if (userId != null) {
                builder.append(" and mu.id = :userId");
            }
        }

        if (!isCount) {
            builder.append(" limit :start, :limit");
        }

        return builder.toString();
    }
}
