package com.finshare.analytics.repository;

import com.finshare.analytics.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, String> {
    List<Budget> findByUserId(String userId);
    List<Budget> findByUserIdAndCategory(String userId, String category);
}