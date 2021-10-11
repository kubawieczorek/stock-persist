package com.jw.stockprocessor.configuration;

import com.jw.stockprocessor.model.DailyStockChange;
import org.springframework.batch.item.excel.RowMapper;
import org.springframework.batch.item.excel.support.rowset.RowSet;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

public class StockRowMapper implements RowMapper {

    @Override
    public Object mapRow(RowSet rs) throws Exception {
        String[] currentRow = rs.getCurrentRow();
        DailyStockChange dailyStockChange = new DailyStockChange();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        dailyStockChange.setDate(format.parse(currentRow[0]));
        dailyStockChange.setName(currentRow[1]);
        dailyStockChange.setIsin(currentRow[2]);
        dailyStockChange.setCurrency(currentRow[3]);
        dailyStockChange.setOpeningRate(new BigDecimal(currentRow[4]));
        dailyStockChange.setMaxRate(new BigDecimal(currentRow[5]));
        dailyStockChange.setMinRate(new BigDecimal(currentRow[6]));
        dailyStockChange.setCloseRate(new BigDecimal(currentRow[7]));
        dailyStockChange.setDelta(new BigDecimal(currentRow[8]));
        dailyStockChange.setVolume(new BigDecimal(currentRow[9]));
        dailyStockChange.setTransactions(new BigDecimal(currentRow[10]));
        dailyStockChange.setTurnover(new BigDecimal(currentRow[11]));
        return dailyStockChange;
    }
}
