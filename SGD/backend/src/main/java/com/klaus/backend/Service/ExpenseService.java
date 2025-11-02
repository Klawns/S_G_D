package com.klaus.backend.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.klaus.backend.DTO.Mapper.ExpenseMapper;
import com.klaus.backend.DTO.request.ExpensesRequestDTO;
import com.klaus.backend.Exception.ResourceNotFoundException;
import com.klaus.backend.Model.Expenses;
import com.klaus.backend.Model.User;
import com.klaus.backend.Repository.ExpensesRepository;
import com.klaus.backend.Repository.UserRepository;
import com.klaus.backend.Repository.QueryFilter.ExpensesQueryFilter;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final ExpensesRepository expensesRepository;
    private final UserRepository userRepository;
    private final ExpenseMapper mapper;

    public Page<Expenses> listExpenses(Pageable pageable, ExpensesQueryFilter filter) {
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            filter.setUsername(username);
            return expensesRepository.findAll(filter.toSpecification(), pageable);
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao listar despesas: " + e.getMessage());
        }

    }

    public Expenses createExpense(ExpensesRequestDTO req) {
        try {
            Expenses expense = mapper.toEntity(req);

            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

            expense.setUser(user);

            return expensesRepository.save(expense);
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao salvar despesa: " + e.getMessage());
        }
    }

    @Transactional
    public Expenses updateExpense(Long id, ExpensesRequestDTO req) {
        try {
            Expenses existingExpense = expensesRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Despesa", id));
            mapper.updateEntityFromDto(req, existingExpense);
            return expensesRepository.save(existingExpense);
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao atualizar despesa: " + e.getMessage());
        }
    }

    @Transactional
    public void deleteExpense(Long id) {
        Expenses existingExpense = expensesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Despesa", id));

        expensesRepository.delete(existingExpense);
    }

}
