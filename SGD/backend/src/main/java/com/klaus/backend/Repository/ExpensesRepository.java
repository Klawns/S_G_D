package com.klaus.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.klaus.backend.Model.Expenses;

public interface ExpensesRepository extends JpaRepository<Expenses, Long>, JpaSpecificationExecutor<Expenses> {
}