package com.klaus.backend.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PageMargin;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.klaus.backend.Model.Expenses;
import com.klaus.backend.Repository.ExpensesRepository;
import com.klaus.backend.Repository.QueryFilter.ExpensesQueryFilter;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final ExpensesRepository expensesRepository;

    private static final String PREFERRED_FONT = "Roboto Mono";

    // Capitaliza a primeira letra
    public static String capitalize(String str) {
        if (str == null || str.isEmpty())
            return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * Gera o relatório XLSX com a lista de despesas.
     * Retorna um ByteArrayInputStream com o arquivo XLSX pronto para download.
     */
    public ByteArrayInputStream gerarRelatorio(List<Expenses> expenses) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Despesas");
            sheet.setHorizontallyCenter(true);

            // Título / Banner
            Font titleFont = workbook.createFont();
            titleFont.setFontName(PREFERRED_FONT);
            titleFont.setFontHeightInPoints((short) 20);
            titleFont.setBold(true);

            CellStyle titleStyle = workbook.createCellStyle();
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Cabeçalho (fundo escuro)
            Font headerFont = workbook.createFont();
            headerFont.setFontName(PREFERRED_FONT);
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 11);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            addBorders(headerStyle);

            // Fonte para dados
            Font dataFont = workbook.createFont();
            dataFont.setFontName(PREFERRED_FONT);

            // Linhas zebradas (odd/even)
            CellStyle oddRowStyle = workbook.createCellStyle();
            oddRowStyle.setFont(dataFont);
            oddRowStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            addBorders(oddRowStyle);

            CellStyle evenRowStyle = workbook.createCellStyle();
            evenRowStyle.setFont(dataFont);
            evenRowStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            evenRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            evenRowStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            addBorders(evenRowStyle);

            // Estilo de moeda (com R$).
            DataFormat df = workbook.createDataFormat();
            CellStyle currencyStyle = workbook.createCellStyle();
            currencyStyle.setFont(dataFont);
            currencyStyle.setDataFormat(df.getFormat("\"R$\" #,##0.00"));
            currencyStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            currencyStyle.setAlignment(HorizontalAlignment.RIGHT);
            addBorders(currencyStyle);

            // Estilo de data
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setFont(dataFont);
            dateStyle.setDataFormat(df.getFormat("dd/MM/yyyy"));
            dateStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            dateStyle.setAlignment(HorizontalAlignment.CENTER);
            addBorders(dateStyle);

            // Estilos para total
            Font totalFont = workbook.createFont();
            totalFont.setFontName(PREFERRED_FONT);
            totalFont.setBold(true);

            CellStyle totalLabelStyle = workbook.createCellStyle();
            totalLabelStyle.setFont(totalFont);
            totalLabelStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            totalLabelStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            totalLabelStyle.setAlignment(HorizontalAlignment.LEFT);
            totalLabelStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            totalLabelStyle.setFont(headerFont);
            addBorders(totalLabelStyle);

            CellStyle totalValueStyle = workbook.createCellStyle();
            totalValueStyle.cloneStyleFrom(currencyStyle);
            Font totalValueFont = workbook.createFont();
            totalValueFont.setBold(true);
            totalValueFont.setFontName(PREFERRED_FONT);
            totalValueStyle.setFont(totalValueFont);
            totalValueStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            totalValueStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            addBorders(totalValueStyle);

            // Título
            Row titleRow = sheet.createRow(0);
            titleRow.setHeightInPoints(30);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Relatório de Despesas");
            titleCell.setCellStyle(titleStyle);

            // Mescla o banner nas colunas 0..5 (ajustável)
            sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 5));

            // Cabeçalho fixo
            List<String> columns = List.of(
                    "Descrição",
                    "Categoria",
                    "Forma de Pagamento",
                    "Parcelas",
                    "Valor",
                    "Data");

            String[] headerEmojis = {
                    "📝 Descrição",
                    "📂 Categoria",
                    "💳 Forma de Pagamento",
                    "🔢 Parcelas",
                    "💰 Valor",
                    "📅 Data"
            };

            int headerRowNum = 2;
            Row header = sheet.createRow(headerRowNum);
            header.setHeightInPoints(22);

            // preencher cabeçalho (texto ou emoji)
            for (int i = 0; i < columns.size(); i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headerEmojis[i]);
                cell.setCellStyle(headerStyle);
            }

            // Linhas de dados
            int rowIdx = headerRowNum + 1;
            int startDataRow = rowIdx;
            BigDecimal totalValor = BigDecimal.ZERO;

            for (int i = 0; i < expenses.size(); i++) {
                Expenses e = expenses.get(i);
                Row row = sheet.createRow(rowIdx++);

                boolean even = (i % 2 == 1);
                CellStyle rowStyle = even ? evenRowStyle : oddRowStyle;

                int col = 0;

                // Descrição
                Cell c0 = row.createCell(col++);
                c0.setCellValue(capitalize(Objects.toString(e.getDescription(), "")));
                c0.setCellStyle(rowStyle);

                // Categoria
                Cell c1 = row.createCell(col++);
                c1.setCellValue(capitalize(Objects.toString(e.getCategory(), "")));
                c1.setCellStyle(rowStyle);

                // Forma de Pagamento
                Cell c2 = row.createCell(col++);
                c2.setCellValue(capitalize(Objects.toString(e.getPaymentMethod(), "")));
                c2.setCellStyle(rowStyle);

                // Parcelas (campo existe no modelo; é opcional)
                Cell c3 = row.createCell(col++);
                if (e.getInstallments() != null && !e.getInstallments().isBlank()) {
                    c3.setCellValue(e.getInstallments());
                } else {
                    c3.setCellValue("");
                }
                c3.setCellStyle(rowStyle);

                // Valor (número)
                Cell c4 = row.createCell(col++);
                if (e.getAmount() != null) {
                    c4.setCellValue(e.getAmount());
                    totalValor = totalValor.add(BigDecimal.valueOf(e.getAmount()));
                } else {
                    c4.setCellValue(0.0);
                }
                c4.setCellStyle(currencyStyle);

                // Data
                Cell c5 = row.createCell(col++);
                if (e.getDate() != null) {
                    Date date = java.util.Date
                            .from(e.getDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
                    c5.setCellValue(date);
                } else {
                    c5.setCellValue("");
                }
                c5.setCellStyle(dateStyle);
            }

            int endDataRow = rowIdx - 1;

            // Linha de TOTAL — usa fórmula SUM dinâmica na coluna "Valor"
            int valorIndex = columns.indexOf("Valor"); // índice 0-based (esperado 4)
            String colLetter = toExcelColumnLetter(valorIndex); // ex: 4 -> "E"
            int formulaStart = startDataRow + 1; // Excel é 1-based nas fórmulas
            int formulaEnd = endDataRow + 1;
            String sumFormula = String.format("SUM(%s%d:%s%d)", colLetter, formulaStart, colLetter, formulaEnd);

            Row totalRow = sheet.createRow(rowIdx + 1);
            Cell totalLabelCell = totalRow.createCell(0);
            totalLabelCell.setCellValue("TOTAL:");
            totalLabelCell.setCellStyle(totalLabelStyle);

            Cell totalValueCell = totalRow.createCell(valorIndex);
            totalValueCell.setCellFormula(sumFormula);
            totalValueCell.setCellStyle(totalValueStyle);

            // Ajuste automático das colunas baseado no conteúdo + padding extra
            for (int i = 0; i < columns.size(); i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1500);
            }

            // Avalia todas as fórmulas antes de salvar
            workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();

            sheet.getPrintSetup().setLandscape(true);
            sheet.setMargin(PageMargin.LEFT, 0.5);
            sheet.setMargin(PageMargin.RIGHT, 0.5);
            sheet.setMargin(PageMargin.TOP, 0.7);
            sheet.setMargin(PageMargin.BOTTOM, 0.7);

            // grava e retorna stream
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    // Adiciona bordas THIN ao estilo fornecido (top, bottom, left, right).
    private void addBorders(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    /**
     * Converte índice 0-based para letra de coluna do Excel:
     * 0 -> A, 1 -> B, ..., 25 -> Z, 26 -> AA, etc.
     */
    private static String toExcelColumnLetter(int zeroBasedIndex) {
        int col = zeroBasedIndex;
        StringBuilder sb = new StringBuilder();
        while (col >= 0) {
            int rem = col % 26;
            sb.insert(0, (char) ('A' + rem));
            col = (col / 26) - 1;
        }
        return sb.toString();
    }

    public byte[] generateReport(ExpensesQueryFilter filter) throws IOException {
        Specification<Expenses> spec = filter.toSpecification();
        List<Expenses> despesas = expensesRepository.findAll(spec, Sort.by("date").ascending());
        try (ByteArrayInputStream in = gerarRelatorio(despesas)) {
            return in.readAllBytes();
        }
    }
}
