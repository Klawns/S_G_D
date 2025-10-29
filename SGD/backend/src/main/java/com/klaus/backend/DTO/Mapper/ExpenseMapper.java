package com.klaus.backend.DTO.Mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.klaus.backend.DTO.request.ExpensesRequestDTO;
import com.klaus.backend.Model.Expenses;

@Mapper(componentModel = "spring")
public interface ExpenseMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Expenses toEntity(ExpensesRequestDTO dto);

    // Atualiza os campos da entidade existente com os valores do DTO
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntityFromDto(ExpensesRequestDTO dto, @MappingTarget Expenses entity);

}
