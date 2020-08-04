package com.microyum.dao.jdbc;

import com.microyum.model.bond.MyBondBase;
import com.microyum.model.stock.MyStockBase;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class MyBondJdbcDao {

    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<MyBondBase> referBondBaseList(int pageNo, int pageSize, String bond) {

        int start = (pageNo - 1) * pageSize;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("start", start);
        parameters.addValue("pageSize", pageSize);

        StringBuilder builder = new StringBuilder();
        builder.append(" select * from my_bond_base ");

        if (StringUtils.isNotBlank(bond)) {
            builder.append(" where zq_code = :code ");
            parameters.addValue("code", bond);
            builder.append(" or zq_name = :name");
            parameters.addValue("name", bond);
        }

        return namedParameterJdbcTemplate.query(builder.toString(), parameters, new BeanPropertyRowMapper(MyBondBase.class));
    }

    /**
     * 获取已列入观察的股票清单
     *
     * @return
     */
    public List<MyBondBase> getObservedList() {

        String sql = "select * from `my_bond_base` where `observe` = true";

        return jdbcTemplate.query(sql, new Object[]{}, new BeanPropertyRowMapper(MyBondBase.class));
    }

}
