package org.example.template;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Minimal CSV loader for headered files used in JavaFX template.
 * Adjust DELIMITER and column mapping to your CSV.
 */
public class CsvReader {
    private static final String DELIMITER = ";"; // Set to "," if needed
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<DataRecord> load(Reader reader) throws IOException {
        try (BufferedReader br = new BufferedReader(reader)) {
            List<DataRecord> rows = new ArrayList<>();
            // Skip header
            String line = br.readLine();
            if (line == null) {
                return rows;
            }
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(DELIMITER, -1);
                if (parts.length < 5) {
                    continue; // skip malformed rows
                }
                String primary = parts[0].trim();
                String secondary = parts[1].trim();
                LocalDate startDate = LocalDate.parse(parts[2].trim(), FORMATTER);
                LocalDate endDate = LocalDate.parse(parts[3].trim(), FORMATTER);
                int metric = Integer.parseInt(parts[4].trim());
                rows.add(new DataRecord(primary, secondary, startDate, endDate, metric));
            }
            return rows;
        }
    }
}
