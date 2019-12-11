package com.microyum.dao;

import com.microyum.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MyUserJdbcDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<UserDTO> findByNameOrNickName(int start, int limit, String name) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("start", start);
        parameters.addValue("limit", limit);
        parameters.addValue("name", "%" + name + "%");

        return namedParameterJdbcTemplate.query(this.findUserInfoSql(true), parameters, (rs, rowNum) -> {
            UserDTO userDTO = new UserDTO();
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

    public List<UserDTO> findUserInfoPaging(int start, int limit) {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("start", start);
        parameters.addValue("limit", limit);

        return namedParameterJdbcTemplate.query(this.findUserInfoSql(false), parameters, (rs, rowNum) -> {
            UserDTO userDTO = new UserDTO();
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

    private String findUserInfoSql(boolean hasCondition) {
        StringBuilder builder = new StringBuilder();
        builder.append("select mu.id as id, mu.name as name, mu.nick_name as nick_name, mu.email as email, ");
        builder.append(" mu.telephone as telephone, DATE_FORMAT(mu.last_update_time,'%Y-%m-%d %H:%i:%s') as last_update_time, ");
        builder.append(" mr.name as role_name, mr.id as role_id, case mu.locked when true then 'Yes' else 'No' end as locked ");
        builder.append(" from my_user mu inner join my_user_role ur on ur.user_id = mu.id ");
        builder.append(" inner join my_role mr on mr.id = ur.role_id ");
        if (hasCondition) {
            builder.append(" where mu.name like :name or mu.nick_name like :name ");
        }
        builder.append(" limit :start, :limit");

        return builder.toString();
    }
}
