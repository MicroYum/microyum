package com.microyum.dao;

import com.microyum.common.util.StringUtils;
import com.microyum.dto.TraderAccountDto;
import com.microyum.model.MyFinanceTraderAccount;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class MyFinanceDao {

    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<TraderAccountDto> traderAccountOverview(int start, int limit, String trader) {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        StringBuilder builder = new StringBuilder();
        builder.append("select ta.id, mu.id as uid, mu.name, mu.nick_name, ta.trader, ta.account ");
        builder.append(" from my_user mu inner join my_finance_trader_account ta on mu.id = ta.uid ");
        if (StringUtils.isNotBlank(trader)) {
            builder.append(" where trader like :trader ");
            parameters.addValue("trader", "%" + trader + "%");
        }
        builder.append(" limit :start, :limit ");

        parameters.addValue("start", start);
        parameters.addValue("limit", limit);

        return namedParameterJdbcTemplate.query(builder.toString(), parameters, (rs, rowNum) -> {
            TraderAccountDto dto = new TraderAccountDto();
            dto.setId(rs.getLong("id"));
            dto.setUserId(rs.getLong("uid"));
            dto.setUserName(rs.getString("name"));
            dto.setNickName(rs.getString("nick_name"));
            dto.setTrader(rs.getString("trader"));
            dto.setAccount(rs.getString("account"));
            return dto;
        });
    }

    public int traderAccountCreate(TraderAccountDto dto) {

        String sql = "insert into my_finance_trader_account(account, trader, uid) values (?, ?, ?)";
        return jdbcTemplate.update(sql, new Object[]{dto.getAccount(), dto.getTrader(), dto.getUserId()});
    }

    public int traderAccountUpdate(TraderAccountDto dto) {

        String sql = "update my_finance_trader_account set account = ?, trader = ? where id = ?";
        return jdbcTemplate.update(sql, new Object[]{dto.getAccount(), dto.getTrader(), dto.getId()});
    }

    public int traderAccountDelete(Long id) {

        String sql = "delete from my_finance_trader_account where id = ?";
        return jdbcTemplate.update(sql, new Object[]{id});
    }
}
