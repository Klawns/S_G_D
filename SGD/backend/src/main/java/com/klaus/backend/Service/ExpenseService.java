package com.klaus.backend.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.klaus.backend.DTO.Mapper.ExpenseMapper;
import com.klaus.backend.DTO.request.ExpensesRequestDTO;
import com.klaus.backend.Model.Expenses;
import com.klaus.backend.Repository.ExpensesRepository;
import com.klaus.backend.Repository.QueryFilter.ExpensesQueryFilter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpensesRepository expensesRepository;
    private final ExpenseMapper mapper;

    public Page<Expenses> listExpenses(Pageable pageable, ExpensesQueryFilter filter) {
        try {
            return expensesRepository.findAll(filter.toSpecification(), pageable);
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao listar despesas: " + e.getMessage());
        }

    }

    public Expenses createExpense(ExpensesRequestDTO req) {
        try {
            Expenses expense = mapper.toEntity(req);
            return expensesRepository.save(expense);
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao salvar despesa: " + e.getMessage());
        }
    }

    public Expenses updateExpense(Long id, ExpensesRequestDTO req) {
        try {
            Expenses existingExpense = expensesRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Despesa não encontrada"));
            mapper.updateEntityFromDto(req, existingExpense);
            return expensesRepository.save(existingExpense);
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao atualizar despesa: " + e.getMessage());
        }
    }

    public void deleteExpense(Long id) {
        Expenses existingExpense = expensesRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Despesa não encontrada"));

        expensesRepository.delete(existingExpense);
    }

}
