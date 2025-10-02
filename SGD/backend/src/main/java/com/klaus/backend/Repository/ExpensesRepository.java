package com.klaus.backend.Repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.klaus.backend.Model.Expenses;

public interface ExpensesRepository extends JpaRepository<Expenses, Long> {
    List<Expenses> findByUserId(UUID userId);
}