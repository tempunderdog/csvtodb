import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by I831828 on 6/17/2018.
 */
public class ReadCSVForMappingFindings {

    public static Collection<TableAndColumnMappingInfo> readMappingFile(final String fileName) throws Exception {
        Map<String, TableAndColumnMappingInfo> result = new HashMap<>();
        try {
            File file = new File(fileName);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("--")) {
                    //will ignore will consider it as comments
                } else {
                    String[] v = line.split(",");
                    if (v.length <= 2) {
                        //error the row written in CSV is not separated by comma
                        throw new Exception("CSV mapping not right, check line " + line);
                    } else {
                        String csvColumnName = v[0];
                        String dbColumnaName = v[1];
                        String dbColumnaType = v[2];
                        boolean dbColumnaisPK = false;
                        if (v.length == 4 && v[3] != null && v[3].trim().length() > 0) {
                            dbColumnaisPK = Boolean.valueOf(v[3]);
                        }
                        //Will split db column name with .
                        String[] g = dbColumnaName.split("\\.");
                        if (g.length < 2) {
                            //error again as per agreement it should be like TABLE_NAME.COLUMN_NAME
                        } else {
                            String dbTableName = g[0];
                            String dbTableColumnName = g[1];

                            TableAndColumnMappingInfo tableAndColumnMappingInfo = result.get(dbTableName);
                            if (tableAndColumnMappingInfo == null) {
                                tableAndColumnMappingInfo = new TableAndColumnMappingInfo();
                                tableAndColumnMappingInfo.setTableName(dbTableName);
                            }
                            tableAndColumnMappingInfo.addColumnMapping(csvColumnName, dbTableColumnName, dbColumnaType);
                            if (dbColumnaisPK){
                                tableAndColumnMappingInfo.addPkColumnMapping(csvColumnName, dbTableColumnName, dbColumnaType);
                            }
                            result.put(dbTableName, tableAndColumnMappingInfo);
                        }
                    }
                }
            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.values();
    }
}
