package com.csvtool.csvsqltool.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@RestController
@RequestMapping("/api")
public class CsvController {

    @PostMapping("/import-csv")
    public ResponseEntity<String> importCsv(@RequestParam("csvFile") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Empty fail");
        }

        String fileName = file.getOriginalFilename();

//        System.out.println("Received file: " + fileName);

        try {
            InputStream is = file.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            // Header
            String headerLine = br.readLine();
            String[] headers = headerLine.split(",");
            System.out.println("HEADER:");
            for (String h : headers) {
                System.out.println(h);
            }

            // Rows
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                System.out.println("ROW:");
                for (String cell : row) {
                    if (cell.matches("\\d+")) {
                        System.out.println(cell + "is INT");
                    } else if (cell.matches("\\d+\\.\\d+")) {
                        System.out.println(cell + "is DOUBLE");
                    } else if (cell.equalsIgnoreCase("true") || cell.equalsIgnoreCase("false")) {
                        System.out.println(cell + "is BOOLEAN");
                    } else {
                        System.out.println(cell + "is String");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error reading file");
        }

        return ResponseEntity.ok("The file is received: " + fileName);
    }

}
