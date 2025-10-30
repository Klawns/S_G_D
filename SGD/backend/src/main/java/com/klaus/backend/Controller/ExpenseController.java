package com.klaus.backend.Controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.klaus.backend.DTO.request.ExpensesRequestDTO;
import com.klaus.backend.Model.Expenses;
import com.klaus.backend.Repository.QueryFilter.ExpensesQueryFilter;
import com.klaus.backend.Service.ExpenseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseService expenseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Expenses> register(@Valid @RequestBody ExpensesRequestDTO req) {
        return ResponseEntity.ok(expenseService.createExpense(req));
    }

    @GetMapping
    public Page<Expenses> list(Pageable pageable, @Valid ExpensesQueryFilter filter) {
        return expenseService.listExpenses(pageable, filter);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Expenses> update(
            @PathVariable Long id,
            @Valid @RequestBody ExpensesRequestDTO dto) {
        return ResponseEntity.ok(expenseService.updateExpense(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.noContent().build();
    }
}
