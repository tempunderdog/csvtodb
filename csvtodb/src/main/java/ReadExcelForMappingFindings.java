import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by I831828 on 6/17/2018.
 */
public class ReadExcelForMappingFindings {

    public static Collection<TableAndColumnMappingInfo> readMappingFile(final String fileName) throws Exception {
        Map<String, TableAndColumnMappingInfo> result = new HashMap<>();
        try {
            FileInputStream inputStream = new FileInputStream(new File(fileName));
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet firstSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = firstSheet.iterator();
            while (iterator.hasNext()) {
                Row nextRow = iterator.next();
                if(nextRow.getRowNum()==0 ){
                    continue; //just skip the header row
                }
                Iterator<Cell> cellIterator = nextRow.cellIterator();
                String csvColumnaName = cellIterator.next().getStringCellValue();
                String tablename = cellIterator.next().getStringCellValue();
                String tableColName = cellIterator.next().getStringCellValue();
                String tableColDataType = cellIterator.next().getStringCellValue();
                Boolean isPkCol = cellIterator.next().getBooleanCellValue();
                TableAndColumnMappingInfo tableAndColumnMappingInfo = result.get(tablename);
                if (tableAndColumnMappingInfo == null) {
                    tableAndColumnMappingInfo = new TableAndColumnMappingInfo();
                    tableAndColumnMappingInfo.setTableName(tablename);
                }
                tableAndColumnMappingInfo.addColumnMapping(csvColumnaName, tableColName, tableColDataType);
                if (isPkCol != null && Boolean.valueOf(isPkCol)){
                    tableAndColumnMappingInfo.addPkColumnMapping(csvColumnaName, tableColName, tableColDataType);
                }
                result.put(tablename, tableAndColumnMappingInfo);
            }

            workbook.close();
            inputStream.close();
        } catch (Exception e) {
            throw e;
        }
        return result.values();
    }
}
