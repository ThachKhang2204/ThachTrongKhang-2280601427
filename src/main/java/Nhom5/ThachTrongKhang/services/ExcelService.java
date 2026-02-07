package Nhom5.ThachTrongKhang.services;

import Nhom5.ThachTrongKhang.entities.Book;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelService {

    public byte[] exportBooksToExcel(List<Book> books) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Books");

        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] columns = {"ID", "Tiêu đề", "Tác giả", "Giá", "Danh mục"};
        
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        // Create data rows
        int rowNum = 1;
        for (Book book : books) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(book.getId());
            row.createCell(1).setCellValue(book.getTitle());
            row.createCell(2).setCellValue(book.getAuthor());
            row.createCell(3).setCellValue(book.getPrice());
            row.createCell(4).setCellValue(book.getCategory() != null ? book.getCategory().getName() : "");
        }

        // Auto-size columns
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Write to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        return outputStream.toByteArray();
    }

    public List<BookImportDto> importBooksFromExcel(MultipartFile file) throws IOException {
        List<BookImportDto> books = new ArrayList<>();
        
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();
            
            // Skip header row
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
            
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                
                if (isRowEmpty(row)) {
                    continue;
                }
                
                BookImportDto book = new BookImportDto();
                
                // ID (optional for update)
                Cell idCell = row.getCell(0);
                if (idCell != null && idCell.getCellType() == CellType.NUMERIC) {
                    book.setId((long) idCell.getNumericCellValue());
                }
                
                // Title
                Cell titleCell = row.getCell(1);
                if (titleCell != null) {
                    book.setTitle(getCellValueAsString(titleCell));
                }
                
                // Author
                Cell authorCell = row.getCell(2);
                if (authorCell != null) {
                    book.setAuthor(getCellValueAsString(authorCell));
                }
                
                // Price
                Cell priceCell = row.getCell(3);
                if (priceCell != null && priceCell.getCellType() == CellType.NUMERIC) {
                    book.setPrice(priceCell.getNumericCellValue());
                }
                
                // Category name
                Cell categoryCell = row.getCell(4);
                if (categoryCell != null) {
                    book.setCategoryName(getCellValueAsString(categoryCell));
                }
                
                // Image URL
                Cell imageUrlCell = row.getCell(5);
                if (imageUrlCell != null) {
                    book.setImageUrl(getCellValueAsString(imageUrlCell));
                }
                
                books.add(book);
            }
        }
        
        return books;
    }
    
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
    
    private boolean isRowEmpty(Row row) {
        for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }
    
    // DTO for import
    @lombok.Data
    public static class BookImportDto {
        private Long id;
        private String title;
        private String author;
        private Double price;
        private String categoryName;
        private String imageUrl;
    }
}
