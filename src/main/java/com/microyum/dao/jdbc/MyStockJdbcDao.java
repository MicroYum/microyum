package com.microyum.dao.jdbc;

import com.microyum.bo.BuyingStockBO;
import com.microyum.common.enums.StockStrategyEnum;
import com.microyum.common.enums.StockTypeEnum;
import com.microyum.common.util.DateUtils;
import com.microyum.dto.StockBaseListDto;
import com.microyum.dto.StockLatestDataDto;
import com.microyum.model.stock.MyStockBase;
import com.microyum.model.stock.MyStockData;
import com.microyum.model.stock.MyStockDataDetail;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class MyStockJdbcDao {

    @Resource
    private JdbcTemplate jdbcTemplate;
    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Long countAllStockBase(String stock) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        StringBuilder builder = new StringBuilder();
        builder.append("select count(1) count  from `my_stock_base`");

        if (StringUtils.isNotBlank(stock)) {
            builder.append(" where stock_code like :stockCode or stock_name like :stockName ");
            parameters.addValue("stockCode", "%" + stock + "%");
            parameters.addValue("stockName", "%" + stock + "%");
        }

        return namedParameterJdbcTemplate.queryForObject(builder.toString(), parameters, Long.class);
    }

    public Integer countStockDataByCode(String stockCode, Map<String, BigDecimal> map) {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        StringBuilder builder = new StringBuilder();
        builder.append("select count(1) count  from `my_stock_data` where `stock_code` = :stockCode ");
        parameters.addValue("stockCode", stockCode);

        if (map != null) {

            if (map.containsKey("lowPrice")) {
                builder.append(" and `hfq_close` < :price ");
                parameters.addValue("price", map.get("lowPrice"));
            }
            if (map.containsKey("lowVolume")) {
                builder.append(" and `trade_count` < :volume ");
                parameters.addValue("volume", map.get("lowVolume"));
            }
            if (map.containsKey("highPrice")) {
                builder.append(" and `hfq_close` > :price ");
                parameters.addValue("price", map.get("lowPrice"));
            }
            if (map.containsKey("highVolume")) {
                builder.append(" and `trade_count` > :volume ");
                parameters.addValue("volume", map.get("highVolume"));
            }
        }

        return namedParameterJdbcTemplate.queryForObject(builder.toString(), parameters, Integer.class);
    }

    public List<StockBaseListDto> referStockList(int pageNo, int pageSize, String stock) {

        int start = (pageNo - 1) * pageSize;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("start", start);
        parameters.addValue("pageSize", pageSize);

        StringBuilder builder = new StringBuilder();
        builder.append("SELECT ");
        builder.append(" b.area, b.stock_code, b.stock_name, b.type, b.total_capital,");
        builder.append(" b.circulation_capital, b.daily_date, s.strategy, s.trade_date ");
        builder.append("FROM ");
        builder.append(" my_stock_base b ");
        builder.append(" LEFT JOIN ( SELECT area, stock_code, max( trade_date ) trade_date FROM my_stock_daily_strategy GROUP BY area, stock_code ) t ON t.area = b.area ");
        builder.append(" AND t.stock_code = b.stock_code");
        builder.append(" LEFT JOIN my_stock_daily_strategy s ON s.area = t.area ");
        builder.append(" AND s.stock_code = t.stock_code ");
        builder.append(" AND s.trade_date = t.trade_date ");

        if (StringUtils.isNotBlank(stock)) {
            builder.append(" where ");
            builder.append(" b.stock_code like :stockCode ");
            parameters.addValue("stockCode", "%" + stock + "%");
            builder.append(" or ");
            builder.append(" b.stock_name like :stockName ");
            parameters.addValue("stockName", "%" + stock + "%");
        }

        builder.append(" order by ");
        builder.append("    s.strategy, b.list_sort desc ");
        builder.append("    limit :start, :pageSize ");

        List<StockBaseListDto> stockBaseList = namedParameterJdbcTemplate.query(builder.toString(), parameters, (rs, rowNum) -> {
            StockBaseListDto stockBase = new StockBaseListDto();
            stockBase.setArea(rs.getString("area"));
            stockBase.setStockCode(rs.getString("stock_code"));
            stockBase.setStockName(rs.getString("stock_name"));
            stockBase.setType(StockTypeEnum.of(rs.getInt("type")).getName());
            stockBase.setCapital(String.format("%s / %s", StringUtils.defaultString(rs.getString("circulation_capital")), StringUtils.defaultString(rs.getString("total_capital"))));
            stockBase.setStartDate(DateUtils.formatDate(rs.getDate("daily_date"), DateUtils.DATE_FORMAT));

            if (rs.getString("strategy") != null) {
                stockBase.setStrategy(StockStrategyEnum.of(Integer.valueOf(rs.getInt("strategy"))).getName());
                stockBase.setStrategyDate(DateUtils.formatDate(rs.getDate("trade_date"), DateUtils.DATE_FORMAT));
            }

            return stockBase;
        });

        return stockBaseList;
    }

    // 获取已列入观察的股票清单
    public List<MyStockBase> getObservedList() {

        String sql = "select * from `my_stock_base` where `observe` = true";

        return jdbcTemplate.query(sql, new Object[]{}, (rs, rowNum) -> {
            MyStockBase stockBase = new MyStockBase();
            stockBase.setStockCode(rs.getString("stock_code"));
            stockBase.setArea(rs.getString("area"));
            return stockBase;
        });
    }

    // 获取已列入观察的股票清单
    public List<MyStockBase> getObservedListNotIndex() {

        String sql = "select * from `my_stock_base` where `observe` = true and `listing_date` is not null";

        return jdbcTemplate.query(sql, new Object[]{}, (rs, rowNum) -> {
            MyStockBase stockBase = new MyStockBase();
            stockBase.setStockCode(rs.getString("stock_code"));
            stockBase.setStockName(rs.getString("stock_name"));
            stockBase.setArea(rs.getString("area"));
            return stockBase;
        });
    }

    public BigDecimal getHighestStock(String stockCode) {
        String sql = "select max(t.hfq_close) from `my_stock_data` t where t.stock_code = ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{stockCode}, BigDecimal.class);
    }

    public BigDecimal getLowestStock(String stockCode) {
        String sql = "select min(t.hfq_close) from `my_stock_data` t where t.stock_code = ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{stockCode}, BigDecimal.class);
    }

    public StockLatestDataDto referLatestStockData(String stockCode) {

        String sql = "select b.stock_code, b.area, b.stock_name, d.`open`, d.`close`, d.high, d.low, d.percent, d.chg, " +
                " d.trade_amount, d.trade_count, d.trade_date, d.hfq_close from my_stock_data d, my_stock_base b where " +
                " d.trade_date = (SELECT max(t.trade_date) from my_stock_data t where t.stock_code = ?) and " +
                " d.stock_code = ? and d.stock_code = b.stock_code";

        List<StockLatestDataDto> list = jdbcTemplate.query(sql, new Object[]{stockCode, stockCode}, (rs, rowNum) -> {
            StockLatestDataDto stockData = new StockLatestDataDto();
            stockData.setStockCode(rs.getString("stock_code"));
            stockData.setArea(rs.getString("area"));
            stockData.setStockName(rs.getString("stock_name"));
            stockData.setOpen(rs.getBigDecimal("open"));
            stockData.setClose(rs.getBigDecimal("close"));
            stockData.setHigh(rs.getBigDecimal("high"));
            stockData.setLow(rs.getBigDecimal("low"));
            stockData.setChg(String.format("%s[%s%%]", rs.getBigDecimal("chg"), rs.getBigDecimal("percent")));
            stockData.setTradeCount(rs.getBigDecimal("trade_count"));
            stockData.setTradeAmount(rs.getBigDecimal("trade_amount"));
            stockData.setTradeDate(rs.getDate("trade_date"));
            stockData.setHfqClose(rs.getBigDecimal("hfq_close"));

            return stockData;
        });

        if (list.size() == 0) {
            return null;
        }

        return list.get(0);
    }

    public List<MyStockData> referStockData(String stockCode, String startDate, String endDate) {

        String sql = "select * from my_stock_data where stock_id = ? and trade_date between ? and ?";

        List<MyStockData> list = jdbcTemplate.query(sql, new Object[]{stockCode, startDate, endDate}, (rs, rowNum) -> {

            MyStockData stockData = new MyStockData();
            stockData.setStockCode(rs.getString("stock_code"));
            stockData.setArea(rs.getString("area"));
            stockData.setTradeDate(rs.getDate("trade_date"));
            stockData.setOpen(rs.getBigDecimal("open"));
            stockData.setClose(rs.getBigDecimal("close"));
            stockData.setHigh(rs.getBigDecimal("high"));
            stockData.setLow(rs.getBigDecimal("low"));
            stockData.setPercent(rs.getBigDecimal("percent"));
            stockData.setChg(rs.getBigDecimal("chg"));
            stockData.setTradeCount(rs.getBigDecimal("trade_count"));
            stockData.setTradeAmount(rs.getBigDecimal("trade_amount"));

            return stockData;
        });

        return list;
    }

    public MyStockData selectTradeDateStock(String stockCode, String area, Date tradeDate) {

        String sql = "select * from my_stock_data where stock_code = ? and area = ? and trade_date = ?";

        List<MyStockData> list = jdbcTemplate.query(sql, new Object[]{stockCode, area, tradeDate}, (rs, rowNum) -> {
            MyStockData stockData = new MyStockData();
            stockData.setId(rs.getLong("id"));
            stockData.setStockCode(rs.getString("stock_code"));
            stockData.setArea(rs.getString("area"));
            stockData.setTradeDate(rs.getDate("trade_date"));
            stockData.setOpen(rs.getBigDecimal("open"));
            stockData.setClose(rs.getBigDecimal("close"));
            stockData.setHigh(rs.getBigDecimal("high"));
            stockData.setLow(rs.getBigDecimal("low"));
            stockData.setPercent(rs.getBigDecimal("percent"));
            stockData.setChg(rs.getBigDecimal("chg"));
            stockData.setTradeCount(rs.getBigDecimal("trade_count"));
            stockData.setTradeAmount(rs.getBigDecimal("trade_amount"));

            return stockData;
        });

        if (list.size() == 0) {
            return null;
        }

        return list.get(0);
    }

    public List<MyStockDataDetail> referStockTradeDayDetail(String stockCode, String tradeDate) {

        String sql = "select * from my_stock_data_detail where stock_code = ? and date_format(trade_datetime, '%Y-%m-%d') = ?";

        List<MyStockDataDetail> list = jdbcTemplate.query(sql, new Object[]{stockCode, tradeDate}, (rs, rowNum) -> {
            MyStockDataDetail detail = new MyStockDataDetail();
            detail.setStockCode(rs.getString("stock_code"));
            detail.setTradeDatetime(rs.getDate("trade_date_time"));
            detail.setCurrent(rs.getBigDecimal("current"));
            detail.setTradeCount(rs.getBigDecimal("trade_count"));
            detail.setTradeAmount(rs.getBigDecimal("trade_amount"));
            return detail;
        });

        return list;
    }

    public List<BuyingStockBO> referBuyingStock() {
        StringBuilder builder = new StringBuilder();
        builder.append(" SELECT ");
        builder.append("    t.stock_code ");
        builder.append("    ,t.area ");
        builder.append("    ,b.stock_name ");
        builder.append("    ,t.latest_price ");
        builder.append("    ,t.latest_hfq_price ");
        builder.append("    ,t.buying_min ");
        builder.append("    ,t.buying_max ");
        builder.append("    ,t.under_min ");
        builder.append("    ,t.under_max ");
        builder.append("    ,t.trade_count ");
        builder.append("    ,t.volume_rate ");
        builder.append(" FROM my_stock_daily_strategy t ");
        builder.append(" LEFT JOIN my_stock_base b ON t.stock_code = b.stock_code ");
        builder.append(" WHERE t.strategy = 1 and date_format( t.trade_date, '%Y-%m-%d' ) = date_format( now(), '%Y-%m-%d' )  ");
        builder.append(" ORDER BY t.trade_date DESC, t.stock_code ASC ");

        return jdbcTemplate.query(builder.toString(), new Object[]{}, (rs, rowNum) -> {
            BuyingStockBO bo = new BuyingStockBO();
            bo.setStockCode(rs.getString("stock_code"));
            bo.setArea(rs.getString("area"));
            bo.setStockName(rs.getString("stock_name"));
            bo.setLatestPrice(rs.getBigDecimal("latest_price"));
            bo.setLatestHfqPrice(rs.getBigDecimal("latest_hfq_price"));
            bo.setBuyingMin(rs.getBigDecimal("buying_min"));
            bo.setBuyingMax(rs.getBigDecimal("buying_max"));
            bo.setUnderMin(rs.getBigDecimal("under_min"));
            bo.setUnderMax(rs.getBigDecimal("under_max"));
            bo.setTradeCount(rs.getInt("trade_count"));
            bo.setVolumeRate(rs.getBigDecimal("volume_rate"));
            return bo;
        });
    }
}
