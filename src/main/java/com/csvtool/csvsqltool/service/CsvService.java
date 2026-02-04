package com.csvtool.csvsqltool.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Service
public class CsvService {
    private static final Logger log = LoggerFactory.getLogger(CsvService.class);

    private String detectColumnType(List<String[]> rows, int colIndex) {
        boolean isInt = true;
        boolean isDouble = true;
        boolean isBool = true;

        for (String[] row : rows) {

            if (colIndex >= row.length) {
                return "VARCHAR";
            }

            String value = row[colIndex].trim();

            if (value.isEmpty()) {
                return "VARCHAR";
            }

            isBool = isBool && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"));
            isInt = isInt && value.matches("\\d+");
            isDouble = isDouble && value.matches("\\d+(\\.\\d+)?");
        }

        if (isBool) return "BOOLEAN";
        if (isInt) return "INT";
        if (isDouble) return "DOUBLE";
        return "VARCHAR";
    }

    public Map<String, Object> parseCsv(InputStream inputStream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        String headerLine = br.readLine();
        if (headerLine == null) {
            throw new IOException("CSV is empty");
        }
        String[] headers = headerLine.split(",");


        // Rows
        List<String[]> rows = new ArrayList<>();
        String line;
        while ((line = br.readLine()) != null) {
            rows.add(line.split(","));
        }

        Map<String, String> columnTypes = new LinkedHashMap<>();
        for (int col = 0; col < headers.length; col++) {
            String type = detectColumnType(rows, col);
            columnTypes.put(headers[col].trim(), type);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("tableName", "temporary_table");
        response.put("headers", headers);
        response.put("columns", columnTypes);
        response.put("rowsCount", rows.size());

        log.info("CSV parsed successfully: {} rows, {} columns", rows.size(), headers.length);

        return response;
    }

}
