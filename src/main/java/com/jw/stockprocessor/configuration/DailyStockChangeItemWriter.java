package com.jw.stockprocessor.configuration;

import com.jw.stockprocessor.model.DailyStockChange;
import com.jw.stockprocessor.repositories.DailyStockChangeRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import java.util.List;

@Component
public class DailyStockChangeItemWriter implements ItemWriter<DailyStockChange> {

    private final DailyStockChangeRepository dailyStockChangeRepository;

    public DailyStockChangeItemWriter(DailyStockChangeRepository dailyStockChangeRepository, EntityManager entityManager) {
        this.dailyStockChangeRepository = dailyStockChangeRepository;
    }

    @Override
    public void write(List<? extends DailyStockChange> items) {
        System.out.println("Persisting chunk of stock data");
        dailyStockChangeRepository.saveAll(items);
    }
}
