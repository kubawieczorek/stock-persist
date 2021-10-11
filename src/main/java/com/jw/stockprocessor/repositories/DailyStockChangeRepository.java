package com.jw.stockprocessor.repositories;

import com.jw.stockprocessor.model.DailyStockChange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailyStockChangeRepository extends JpaRepository<DailyStockChange, Integer> {
}
