package com.klaus.backend.DTO.request;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ExpensesRequestDTO(
                @NotBlank(message = "Descrição é obrigatória") String description,

                @NotNull(message = "Preço é obrigatório") @Positive(message = "O preço deve ser um valor positivo") Double amount,
                @NotNull(message = "Data é obrigatória") LocalDate date,
                @NotBlank(message = "Categoria é obrigatória") String category,
                @NotBlank(message = "Método de pagamento é obrigatória") String paymentMethod,
                @Positive(message = "As parcelas devem ter um valor positivo") int installments) {
}