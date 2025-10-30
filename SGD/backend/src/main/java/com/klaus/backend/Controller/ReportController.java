package com.klaus.backend.Controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.klaus.backend.Repository.QueryFilter.ExpensesQueryFilter;
import com.klaus.backend.Service.RelatorioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/expenses/relatorio")
@RequiredArgsConstructor
public class ReportController {
    private final RelatorioService relatorioService;

    @GetMapping()
    public ResponseEntity<byte[]> downloadRelatorio(
            @RequestParam("dataInicial") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial,
            @RequestParam("dataFinal") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFinal,
            Authentication authentication) throws IOException {

        if (dataInicial == null || dataFinal == null) {
            throw new IllegalArgumentException("Data inicial e final são obrigatórias");
        }

        ExpensesQueryFilter filter = new ExpensesQueryFilter();
        filter.setUsername(authentication.getName());
        filter.setStartDate(dataInicial);
        filter.setEndDate(dataFinal);

        byte[] relatorio = relatorioService.generateReport(filter);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        String filename = String.format("despesas_%s_a_%s.xlsx",
                filter.getStartDate().format(formatter),
                filter.getEndDate().format(formatter));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(relatorio);
    }
}
