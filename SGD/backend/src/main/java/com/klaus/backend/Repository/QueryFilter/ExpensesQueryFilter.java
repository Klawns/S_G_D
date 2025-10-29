package com.klaus.backend.Repository.QueryFilter;

import static com.klaus.backend.Repository.Specifications.ExpensesSpec.*;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.klaus.backend.Model.Expenses;
import com.klaus.backend.Repository.Specifications.ExpensesSpec;

public class ExpensesQueryFilter {
    private String desc, paymentMethod, category;
    private Double amount;
    private LocalDate startDate, endDate;

    public Specification<Expenses> toSpecification() {
        return descriptionContains(desc)
                .and(ExpensesSpec.categoryContains(category))
                .and(ExpensesSpec.paymentMethodContains(paymentMethod))
                .and(ExpensesSpec.hasAmount(amount))
                .and(ExpensesSpec.dateBetween(startDate, endDate));
    }
}
