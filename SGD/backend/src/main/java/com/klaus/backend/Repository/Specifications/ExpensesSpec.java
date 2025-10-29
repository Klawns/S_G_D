package com.klaus.backend.Repository.Specifications;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.ObjectUtils;

import com.klaus.backend.Model.Expenses;

public class ExpensesSpec {
    public static Specification<Expenses> descriptionContains(String desc) {
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(desc)) {
                return null;
            }

            return builder.like(root.get("title"), desc);
        };
    }

    public static Specification<Expenses> categoryContains(String category) {
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(category)) {
                return null;
            }

            return builder.like(root.get("category"), category);
        };
    }

    public static Specification<Expenses> paymentMethodContains(String paymentMethod) {
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(paymentMethod)) {
                return null;
            }

            return builder.like(root.get("paymentMethod"), paymentMethod);
        };
    }

    public static Specification<Expenses> hasAmount(Double amount) {
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(amount)) {
                return null;
            }

            return builder.equal(root.get("amount"), amount);
        };
    }

    public static Specification<Expenses> dateBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, builder) -> {
            if (ObjectUtils.isEmpty(startDate) && ObjectUtils.isEmpty(endDate)) {
                return null;
            }

            if (!ObjectUtils.isEmpty(startDate) && !ObjectUtils.isEmpty(endDate)) {
                return builder.between(root.get("date"), startDate, endDate);
            }
            if (!ObjectUtils.isEmpty(startDate)) {
                return builder.greaterThanOrEqualTo(root.get("date"), startDate);
            }
            return builder.lessThanOrEqualTo(root.get("date"), endDate);
        };
    }

}
