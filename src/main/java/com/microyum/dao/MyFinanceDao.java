package com.microyum.dao;

import com.microyum.common.util.StringUtils;
import com.microyum.dto.AssetAllocationDto;
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

    public Long traderAccountOverviewCount(String trader) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        StringBuilder builder = new StringBuilder();
        builder.append("select count(1) ");
        builder.append(" from my_user mu inner join my_finance_trader_account ta on mu.id = ta.uid ");
        if (StringUtils.isNotBlank(trader)) {
            builder.append(" where trader like :trader ");
            parameters.addValue("trader", "%" + trader + "%");
        }

        return namedParameterJdbcTemplate.queryForObject(builder.toString(), parameters, Long.class);
    }

    public List<TraderAccountDto> traderAccountOverview(int start, int limit, String trader) {

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        StringBuilder builder = new StringBuilder();
        builder.append("select ta.id, mu.id as uid, mu.name, mu.nick_name, ta.trader, ta.account ");
        builder.append(" from my_user mu inner join my_finance_trader_account ta on mu.id = ta.uid ");
        if (StringUtils.isNotBlank(trader)) {
            builder.append(" where trader like :trader ");
            parameters.addValue("trader", "%" + trader + "%");
        }
        builder.append(" order by mu.id ");
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

    public List<MyFinanceTraderAccount> traderAccountByUid(Long uid) {
        String sql = "select * from my_finance_trader_account where uid = ?";
        return jdbcTemplate.query(sql, new Object[]{uid}, (rs, rowNum) -> {
            MyFinanceTraderAccount traderAccount = new MyFinanceTraderAccount();
            traderAccount.setId(rs.getLong("id"));
            traderAccount.setTrader(String.format("%s - %s", rs.getString("trader"), rs.getString("account")));
            return traderAccount;
        });
    }

    public Long assetAllocationOverviewCount(String trader, String stock) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        StringBuilder builder = new StringBuilder();
        builder.append(" select count(1) ");
        builder.append(" from my_finance_config fc ");
        builder.append(" inner join my_finance_trader_account ta on fc.ta_id = ta.id ");
        builder.append(" inner join my_user mu on ta.uid = mu.id ");
        builder.append(" inner join my_stock_base sb on sb.stock_name = fc.name ");

        if (StringUtils.isNotBlank(stock)) {
            builder.append(" and ( sb.stock_code like :stock ");
            builder.append(" or sb.stock_name like :stock )");
            parameters.addValue("stock", "%" + stock + "%");
        }

        builder.append(" where 1 = 1 ");

        if (StringUtils.isNotBlank(trader)) {
            builder.append(" and ta.trader like :trader ");
            parameters.addValue("trader", "%" + trader + "%");
        }

        return namedParameterJdbcTemplate.queryForObject(builder.toString(), parameters, Long.class);
    }

    public List<AssetAllocationDto> assetAllocationOverview(int start, int limit, String trader, String stock) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        StringBuilder builder = new StringBuilder();
        builder.append("select fc.id, fc.`type`, fc.amount, fc.currency, ta.id as ta_id,  ");
        builder.append(" ta.trader, ta.account, mu.id as uid, mu.`name` as user_name, ");
        builder.append(" mu.nick_name, sb.stock_code, fc.name as stock_name ");
        builder.append(" from my_finance_config fc ");
        builder.append(" inner join my_finance_trader_account ta on fc.ta_id = ta.id ");
        builder.append(" inner join my_user mu on ta.uid = mu.id ");
        builder.append(" inner join my_stock_base sb on sb.stock_name = fc.name ");

        if (StringUtils.isNotBlank(stock)) {
            builder.append(" and ( sb.stock_code like :stock ");
            builder.append(" or sb.stock_name like :stock )");
            parameters.addValue("stock", "%" + stock + "%");
        }

        builder.append(" where 1 = 1 ");

        if (StringUtils.isNotBlank(trader)) {
            builder.append(" and ta.trader like :trader ");
            parameters.addValue("trader", "%" + trader + "%");
        }

        builder.append(" order by mu.id ");
        builder.append(" limit :start, :limit ");

        parameters.addValue("start", start);
        parameters.addValue("limit", limit);

        return namedParameterJdbcTemplate.query(builder.toString(), parameters, (rs, rowNum) -> {
            AssetAllocationDto dto = new AssetAllocationDto();
            dto.setId(rs.getLong("id"));
            dto.setAssetType(rs.getString("type"));
            dto.setAmount(rs.getString("amount"));
            dto.setCurrency(rs.getString("currency"));

            dto.setTaId(rs.getLong("ta_id"));
            dto.setTrader(rs.getString("trader"));
            dto.setAccount(rs.getString("account"));

            dto.setUid(rs.getLong("uid"));
            dto.setUserName(rs.getString("user_name"));
            dto.setNickName(rs.getString("nick_name"));

            dto.setAssetName(rs.getString("stock_name"));
            dto.setStockCode(rs.getString("stock_code"));

            return dto;
        });
    }

    public int assetAllocationCreate(AssetAllocationDto dto) {

        String sql = "insert into my_finance_config(amount, currency, `name`, ta_id, `type`) values (?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, new Object[]{dto.getAmount(), dto.getCurrency(), dto.getAssetName(),
                dto.getTaId(), dto.getAssetType()});
    }

    public int assetAllocationUpdate(AssetAllocationDto dto) {

        String sql = "update my_finance_config set amount = ?, currency = ?, `name` = ?, ta_id = ?, `type` = ? where id = ?";
        return jdbcTemplate.update(sql, new Object[]{dto.getAmount(), dto.getCurrency(), dto.getAssetName(),
                dto.getTaId(), dto.getAssetType()});
    }

    public int assetAllocationDelete(Long id) {

        String sql = "delete from my_finance_config where id = ?";
        return jdbcTemplate.update(sql, new Object[]{id});
    }
}
