import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        try {
            Collection<TableAndColumnMappingInfo> coll = ReadExcelForMappingFindings.readMappingFile("C:\\dev\\research\\ReadCSVUpdateDB\\mapping.xlsx");
            parseExcel("C:\\dev\\research\\ReadCSVUpdateDB\\data.xlsx", coll);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* * Method to read CSV file using CSVParser from Apache Commons CSV */
    public static void parseCSV(final String dataFile, final Collection<TableAndColumnMappingInfo> tableMappingInfo) throws FileNotFoundException, IOException {
        CSVParser parser = new CSVParser(new FileReader(dataFile), CSVFormat.DEFAULT.withHeader());
        for (CSVRecord record : parser) {
            Map<String, String> csvRecord = record.toMap();
            for (TableAndColumnMappingInfo tableAndColumnMappingInfo : tableMappingInfo) {
                String sql = "update " + tableAndColumnMappingInfo.getTableName() + " set ";
                for (Map.Entry<String, DatabaseColumn> mapping : tableAndColumnMappingInfo.getColumnMappings().entrySet()) {
                    if (csvRecord.get(mapping.getKey()) != null && csvRecord.get(mapping.getKey()).trim().length() > 0) {
                        sql = sql.concat(mapping.getValue().getDbColName()).concat(" = ").concat("'" + csvRecord.get(mapping.getKey()) + "'").concat(",");
                    }
                }
                sql = sql.substring(0, sql.length() - 1);
                //PK loop
                System.out.println(sql);
            }

        }
        parser.close();
    }

    public static void parseExcel(final String dataFile, final Collection<TableAndColumnMappingInfo> tableMappingInfo) throws FileNotFoundException, IOException {

        try {
            FileInputStream inputStream = new FileInputStream(new File(dataFile));
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet firstSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = firstSheet.iterator();
            List<Map<String, String>> csvRecords = new ArrayList<>();
            while (iterator.hasNext()) {
                Row nextRow = iterator.next();
                if (nextRow.getRowNum() == 0) {
                    continue; //just skip the header row
                }
                Iterator<Cell> cellIterator = nextRow.cellIterator();
                Map<String, String> csvRecord = new HashMap<>();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    String headerName = getCellName(cell, firstSheet);
                    String cellValue = getCellValueAsString(cell);
                    csvRecord.put(headerName, cellValue);
                }
                csvRecords.add(csvRecord);
            }
            workbook.close();
            inputStream.close();

            for (Map<String, String> csvRecord : csvRecords) {
                for (TableAndColumnMappingInfo tableAndColumnMappingInfo : tableMappingInfo) {
                    String sql = "update " + tableAndColumnMappingInfo.getTableName() + " set ";
                    for (Map.Entry<String, DatabaseColumn> mapping : tableAndColumnMappingInfo.getColumnMappings().entrySet()) {
                        if (csvRecord.get(mapping.getKey()) != null && csvRecord.get(mapping.getKey()).trim().length() > 0) {
                            sql = sql.concat(mapping.getValue().getDbColName()).concat(" = ").concat("'" + csvRecord.get(mapping.getKey()) + "'").concat(",");
                        }
                    }
                    sql = sql.substring(0, sql.length() - 1);
                    //PK loop
                    System.out.println(sql);
                }
            }
        } catch (Exception e) {
            throw e;
        }

    }

    private static String getCellName(Cell cell1, Sheet sheet) {
        CellReference cr = new CellReference(CellReference.convertNumToColString(cell1.getColumnIndex()) + "0");
        Row row = sheet.getRow(0);
        Cell cell = row.getCell(cell1.getColumnIndex());
        return cell.getStringCellValue();
    }

    public static String getCellValueAsString(Cell cell) {
        String strCellValue = null;
        if (cell != null) {
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_STRING:
                    strCellValue = cell.toString();
                    break;
                case Cell.CELL_TYPE_NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        strCellValue = dateFormat.format(cell.getDateCellValue());
                    } else {
                        Double value = cell.getNumericCellValue();
                        Long longValue = value.longValue();
                        strCellValue = new String(longValue.toString());
                    }
                    break;
                case Cell.CELL_TYPE_BOOLEAN:
                    strCellValue = new String(new Boolean(
                            cell.getBooleanCellValue()).toString());
                    break;
                case Cell.CELL_TYPE_BLANK:
                    strCellValue = "";
                    break;
            }
        }
        return strCellValue;
    }
}
