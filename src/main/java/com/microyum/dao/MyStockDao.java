package com.microyum.dao;

import com.microyum.dto.StockLatestDataDTO;
import com.microyum.model.MyStockBase;
import com.microyum.model.MyStockData;
import com.microyum.model.MyStockDataDetail;
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

@Repository
public class MyStockDao {

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

    public List<MyStockBase> referStockList(int pageNo, int pageSize, String stock) {

        int start = (pageNo - 1) * pageSize;
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("start", start);
        parameters.addValue("pageSize", pageSize);

        StringBuilder builder = new StringBuilder();
        builder.append("select * ");
        builder.append(" from `my_stock_base`");

        if (StringUtils.isNotBlank(stock)) {
            builder.append(" where ");
            builder.append(" stock_code like :stockCode ");
            parameters.addValue("stockCode", "%" + stock + "%");
            builder.append(" or ");
            builder.append(" stock_name like :stockName ");
            parameters.addValue("stockName", "%" + stock + "%");
        }

        builder.append(" order by ");
        builder.append("    list_sort desc ");
        builder.append("    limit :start, :pageSize ");

        List<MyStockBase> stockBaseList = namedParameterJdbcTemplate.query(builder.toString(), parameters, (rs, rowNum) -> {
            MyStockBase stockBase = new MyStockBase();
            stockBase.setId(rs.getLong("id"));
            stockBase.setStockCode(rs.getString("stock_code"));
            stockBase.setStockName(rs.getString("stock_name"));
            stockBase.setListingDate(rs.getDate("listing_date"));
            stockBase.setArea(rs.getString("area"));
            stockBase.setListSort(rs.getInt("list_sort"));
            stockBase.setCirculationCapital(rs.getDouble("circulation_capital"));
            stockBase.setTotalCapital(rs.getDouble("total_capital"));
            stockBase.setObserve(rs.getByte("observe"));
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
            stockBase.setArea(rs.getString("area"));
            return stockBase;
        });
    }

    public BigDecimal getHighestStock(String stockCode) {
        String sql = "select max(t.hfq_close) from `my_stock_data` t where t.symbol = ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{stockCode}, BigDecimal.class);
    }

    public BigDecimal getLowestStock(String stockCode) {
        String sql = "select min(t.hfq_close) from `my_stock_data` t where t.symbol = ?";

        return jdbcTemplate.queryForObject(sql, new Object[]{stockCode}, BigDecimal.class);
    }

    public StockLatestDataDTO referLatestStockData(String stockCode) {

        String sql = "select b.stock_code, b.stock_name, d.`open`, d.`close`, d.high, d.low, d.percent, d.chg, " +
                " d.trade_amount, d.trade_count, d.trade_date from my_stock_data d, my_stock_base b where " +
                " d.trade_date = (SELECT max(t.trade_date) from my_stock_data t where t.symbol = ?) and " +
                " d.symbol = ? and d.symbol = b.stock_code";

        List<StockLatestDataDTO> list = jdbcTemplate.query(sql, new Object[]{stockCode, stockCode}, (rs, rowNum) -> {
            StockLatestDataDTO stockData = new StockLatestDataDTO();
            stockData.setStockCode(rs.getString("stock_code"));
            stockData.setStockName(rs.getString("stock_name"));
            stockData.setOpen(rs.getBigDecimal("open"));
            stockData.setClose(rs.getBigDecimal("close"));
            stockData.setHigh(rs.getBigDecimal("high"));
            stockData.setLow(rs.getBigDecimal("low"));
            stockData.setChg(String.format("%s[%s%]", rs.getBigDecimal("chg"), rs.getBigDecimal("percent")));
            stockData.setTradeCount(rs.getBigDecimal("trade_count"));
            stockData.setTradeAmount(rs.getBigDecimal("trade_amount"));
            stockData.setTradeDate(rs.getDate("trade_date"));

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
            stockData.setSymbol(rs.getString("symbol"));
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

    public MyStockData selectTradeDateStock(String stockId, Date tradeDate) {

        String sql = "select * from my_stock_data where symbol = ? and trade_date = ?";

        List<MyStockData> list = jdbcTemplate.query(sql, new Object[]{stockId, tradeDate}, (rs, rowNum) -> {
            MyStockData stockData = new MyStockData();
            stockData.setSymbol(rs.getString("symbol"));
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

    public Integer saveStockDataDetail(MyStockDataDetail stockDataDetail) {
        StringBuilder builder = new StringBuilder();
        builder.append("insert into my_stock_data_detail ");
        builder.append(" ( symbol, trade_datetime, current, ");
        builder.append(" trade_count, trade_amount) values ( ");
        builder.append(" ?, ?, ?, ?, ? ");
        builder.append(" ) ");

        List<Object> params = new ArrayList<>();
        params.add(stockDataDetail.getSymbol());
        params.add(stockDataDetail.getTradeDatetime());
        params.add(stockDataDetail.getCurrent());
        params.add(stockDataDetail.getTradeCount());
        params.add(stockDataDetail.getTradeAmount());

        return jdbcTemplate.update(builder.toString(), params.toArray());
    }

    public Integer saveStockData(MyStockData stockData) {
        StringBuilder builder = new StringBuilder();
        builder.append("insert into my_stock_data ");
        builder.append(" (symbol, trade_date, open, ");
        builder.append(" close, high, low, percent, chg, ");
        builder.append(" trade_count, trade_amount) values ( ");
        builder.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        List<Object> params = new ArrayList<>();
        params.add(stockData.getSymbol());
        params.add(stockData.getTradeDate());
        params.add(stockData.getOpen());
        params.add(stockData.getClose());
        params.add(stockData.getHigh());
        params.add(stockData.getLow());
        params.add(stockData.getPercent());
        params.add(stockData.getChg());
        params.add(stockData.getTradeCount());
        params.add(stockData.getTradeAmount());

        return jdbcTemplate.update(builder.toString(), params.toArray());
    }

    public Integer updateStockData(MyStockData stockData) {

        StringBuilder builder = new StringBuilder();
        builder.append("update my_stock_data ");
        builder.append(" set close = :close, ");
        builder.append("  high = :high, ");
        builder.append("  low = :low, ");
        builder.append("  percent = :percent, ");
        builder.append("  chg = :chg, ");
        builder.append("  trade_count = :tradeCount, ");
        builder.append("  trade_amount = :tradeAmount ");
        builder.append(" where symbol = :symbol ");
        builder.append(" and trade_date = :tradeDate");

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("close", stockData.getClose());
        parameters.addValue("high", stockData.getHigh());
        parameters.addValue("low", stockData.getLow());
        parameters.addValue("percent", stockData.getPercent());
        parameters.addValue("chg", stockData.getChg());
        parameters.addValue("tradeCount", stockData.getTradeCount());
        parameters.addValue("tradeAmount", stockData.getTradeAmount());
        parameters.addValue("symbol", stockData.getSymbol());
        parameters.addValue("tradeDate", stockData.getTradeDate());

        return namedParameterJdbcTemplate.update(builder.toString(), parameters);
    }

    public List<MyStockDataDetail> referStockTradeDayDetail(String stockId, String tradeDate) {

        String sql = "select * from my_stock_data_detail where symbol = ? and date_format(trade_datetime, '%Y-%m-%d') = ?";

        List<MyStockDataDetail> list = jdbcTemplate.query(sql, new Object[]{stockId, tradeDate}, (rs, rowNum) -> {
            MyStockDataDetail detail = new MyStockDataDetail();
            detail.setSymbol(rs.getString("symbol"));
            detail.setTradeDatetime(rs.getDate("trade_date_time"));
            detail.setCurrent(rs.getBigDecimal("current"));
            detail.setTradeCount(rs.getBigDecimal("trade_count"));
            detail.setTradeAmount(rs.getBigDecimal("trade_amount"));
            return detail;
        });

        return list;
    }
}
